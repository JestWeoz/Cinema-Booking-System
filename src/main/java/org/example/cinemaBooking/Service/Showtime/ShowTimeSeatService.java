package org.example.cinemaBooking.Service.Showtime;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.DTO.Request.Seat.LockSeatRequest;
import org.example.cinemaBooking.DTO.Request.Seat.UnlockSeatRequest;
import org.example.cinemaBooking.DTO.Response.Showtime.SeatMapResponse;
import org.example.cinemaBooking.DTO.Response.Showtime.ShowtimeSeatResponse;
import org.example.cinemaBooking.Entity.Showtime;
import org.example.cinemaBooking.Entity.ShowtimeSeat;
import org.example.cinemaBooking.Entity.UserEntity;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.ShowtimeSeatMapper;
import org.example.cinemaBooking.Repository.ShowtimeRepository;
import org.example.cinemaBooking.Repository.ShowtimeSeatRepository;
import org.example.cinemaBooking.Repository.UserRepository;
import org.example.cinemaBooking.Shared.enums.SeatStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ShowTimeSeatService {
    ShowtimeSeatRepository showtimeSeatRepository;
    ShowtimeRepository showtimeRepository;
    UserRepository userRepository;
    ShowtimeSeatMapper showtimeSeatMapper;


    private static final int LOCK_DURATION_MINUTES = 10;
    /**
     * Lấy danh sách ghế của suất chiếu, trả về theo hàng để client render.
     */
    @Transactional(readOnly = true)
    public SeatMapResponse getSeatMap(String showtimeId){
        Showtime showtime = getShowtimeOrThrow(showtimeId);

        List<ShowtimeSeat> showtimeSeats = showtimeSeatRepository.
                findAllByShowtimeIdWithDetails(showtime.getId());


        List<ShowtimeSeatResponse> responses = showtimeSeats.stream().
                map(showtimeSeatMapper::toResponse)
                .toList();

        // 4. Gom theo hàng: "A" → [A1, A2, ...]
        Map<String, List<ShowtimeSeatResponse>> seatMap = responses.stream()
                .collect(Collectors.groupingBy(
                        ShowtimeSeatResponse::seatRow,
                        TreeMap::new,
                        Collectors.toList()
                ));

        int availableSeats = (int) responses.stream()
                .filter(s -> s.status() == SeatStatus.AVAILABLE)
                .count();
        return new SeatMapResponse(
                showtimeId,
                responses.size(),
                availableSeats,
                seatMap);
    }

    @Transactional(readOnly = true)
    public List<ShowtimeSeatResponse> getMyLockedSeats(String showtimeId) {
        String userId = getCurrentUserId().getId();


        return showtimeSeatRepository
                .findLockedByShowtimeAndUser(showtimeId, userId)
                .stream()
                .map(showtimeSeatMapper::toResponse)
                .toList();
    }


    @Transactional
    public List<ShowtimeSeatResponse> lockSeats(String showtimeId, LockSeatRequest request){
        String userId = getCurrentUserId().getId();
        Showtime showtime = getShowtimeOrThrow(showtimeId);
        if(!showtime.isBookable()){
            throw new AppException(ErrorCode.SHOWTIME_STATE_INVALID);
        }

        List<ShowtimeSeat> targets = showtimeSeatRepository.
                findByShowtimeIdAndSeatIds(showtimeId, request.seatIds());

        if(targets.size() != request.seatIds().size()) {
            throw new AppException(ErrorCode.SEAT_NOT_FOUND);
        }
        LocalDateTime lockExpiry = LocalDateTime.now()
                .plusMinutes(LOCK_DURATION_MINUTES);

        for (ShowtimeSeat ss : targets) {
            switch (ss.getStatus()) {
                case AVAILABLE -> {
                    // Ghế trống → lock bình thường
                    ss.setStatus(SeatStatus.LOCKED);
                    ss.setLockedByUser(userId);
                    ss.setLockedUntil(lockExpiry);
                }
                case LOCKED -> {
                    if (ss.getLockedUntil() != null &&
                            ss.getLockedUntil().isBefore(LocalDateTime.now())) {

                        // lock hết hạn → release luôn
                        releaseSeat(ss);

                        ss.setStatus(SeatStatus.LOCKED);
                        ss.setLockedByUser(userId);
                        ss.setLockedUntil(lockExpiry);
                        break;
                    }

                    // Ghế đang bị lock bởi chính user này → extend thời gian
                    if (!userId.equals(ss.getLockedByUser())) {
                        throw new AppException(ErrorCode.SEAT_ALREADY_LOCKED);
                    }
                    ss.setLockedUntil(lockExpiry); // extend
                }
                case BOOKED -> throw new AppException(ErrorCode.SEAT_ALREADY_BOOKED);
            }
        }

        showtimeSeatRepository.saveAll(targets);

        log.info("User {} locked {} seat(s) in showtime {} until {}",
                userId, targets.size(), showtimeId, lockExpiry);

        return targets.stream().map(showtimeSeatMapper::toResponse).toList();
    }


    @Transactional
    public List<ShowtimeSeatResponse> unlockSeats(String showtimeId, UnlockSeatRequest request){
        String userId = getCurrentUserId().getId();

        List<ShowtimeSeat> targets = showtimeSeatRepository
                .findByShowtimeIdAndSeatIds(showtimeId, request.seatIds());

        if (targets.size() != request.seatIds().size()) {
            throw new AppException(ErrorCode.SEAT_NOT_FOUND);
        }

        for (ShowtimeSeat ss : targets) {
            if (ss.getStatus() != SeatStatus.LOCKED) {
                throw new AppException(ErrorCode.SEAT_NOT_LOCKED);
            }
            if (!userId.equals(ss.getLockedByUser())) {
                throw new AppException(ErrorCode.SEAT_LOCK_FORBIDDEN);
            }
            releaseSeat(ss);
        }

        // Cập nhật cache availableSeats trên Showtime
        syncAvailableSeats(showtimeId);

        showtimeSeatRepository.saveAll(targets);

        log.info("User {} unlocked {} seat(s) in showtime {}",
                userId, targets.size(), showtimeId);

        return targets.stream().map(showtimeSeatMapper::toResponse).toList();
    }



    /**
     * Confirm booking: chuyển ghế từ LOCKED → BOOKED.
     * Booking service gọi sau khi payment thành công.
     */
    @Transactional
    public void confirmBooking(String showtimeId, List<String> seatIds, String userId) {
        List<ShowtimeSeat> targets = showtimeSeatRepository
                .findByShowtimeIdAndSeatIds(showtimeId, seatIds);

        for (ShowtimeSeat ss : targets) {
            if (ss.getStatus() != SeatStatus.LOCKED || !userId.equals(ss.getLockedByUser())) {
                throw new AppException(ErrorCode.SEAT_LOCK_MISMATCH);
            }
            ss.setStatus(SeatStatus.BOOKED);
            ss.setLockedUntil(null);
            ss.setLockedByUser(null);
        }

        showtimeSeatRepository.saveAll(targets);

        // Cập nhật cache
        syncAvailableSeats(showtimeId);

        log.info("Confirmed booking: {} seat(s) in showtime {} for user {}",
                targets.size(), showtimeId, userId);
    }

    /**
     * Release ghế khi booking bị cancel / hoàn tiền.
     * Booking/Refund service gọi.
     */
    @Transactional
    public void releaseBookedSeats(String showtimeId, List<String> seatIds) {
        List<ShowtimeSeat> targets = showtimeSeatRepository
                .findByShowtimeIdAndSeatIds(showtimeId, seatIds);

        targets.forEach(this::releaseSeat);
        showtimeSeatRepository.saveAll(targets);

        syncAvailableSeats(showtimeId);

        log.info("Released {} seat(s) in showtime {}", targets.size(), showtimeId);
    }

    // ─────────────────────────────────────────────────────────────────
    // SCHEDULED JOB
    // ─────────────────────────────────────────────────────────────────

    /**
     * Dọn lock hết hạn mỗi phút.
     * Bulk UPDATE 1 câu query → không load entity vào memory.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        int count = showtimeSeatRepository.releaseExpiredLocks(now);
        if (count > 0) {
            log.info("Released {} expired seat lock(s) at {}", count, now);
        }
    }


    private void releaseSeat(ShowtimeSeat ss) {
        ss.setStatus(SeatStatus.AVAILABLE);
        ss.setLockedUntil(null);
        ss.setLockedByUser(null);
    }



    /**
     * Đồng bộ lại cache availableSeats trên Showtime entity.
     * Gọi sau mỗi thao tác thay đổi trạng thái ghế.
     */
    private void syncAvailableSeats(String showtimeId) {
        int available = showtimeSeatRepository
                .countByShowtimeIdAndStatus(showtimeId, SeatStatus.AVAILABLE);
        showtimeRepository.findById(showtimeId).ifPresent(s -> {
            s.setAvailableSeats(available);
            showtimeRepository.save(s);
        });
    }


    //Internal
    private Showtime getShowtimeOrThrow(String showtimeId){
        return showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));
    }

    private UserEntity getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findUserEntityByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}

