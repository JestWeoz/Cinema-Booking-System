// TicketService.java
package org.example.cinemaBooking.Service.Ticket;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.DTO.Request.Ticket.CheckInRequest;
import org.example.cinemaBooking.DTO.Response.Ticket.CheckInResponse;
import org.example.cinemaBooking.DTO.Response.Ticket.TicketResponse;
import org.example.cinemaBooking.Entity.Booking;
import org.example.cinemaBooking.Entity.Ticket;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.TicketMapper;
import org.example.cinemaBooking.Repository.BookingRepository;
import org.example.cinemaBooking.Repository.TicketRepository;
import org.example.cinemaBooking.Shared.enums.BookingStatus;
import org.example.cinemaBooking.Shared.untils.QRCodeUtil;
import org.example.cinemaBooking.Shared.enums.ShowTimeStatus;
import org.example.cinemaBooking.Shared.enums.TicketStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class TicketService {

    TicketRepository  ticketRepository;
    BookingRepository bookingRepository;
    TicketMapper      ticketMapper;

    // ─────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────

    /** Lấy tất cả vé của 1 booking — user chỉ xem được booking của mình */
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByBooking(String bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
            .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!booking.getUser().getId().equals(getCurrentUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new AppException(ErrorCode.BOOKING_STATUS_INVALID);
        }

        return ticketRepository.findAllByBookingId(bookingId)
            .stream().map(ticketMapper::toResponse).toList();
    }

    /** Lấy tất cả vé của user hiện tại */
    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets() {
        return ticketRepository.findAllByUserId(getCurrentUserId())
            .stream().map(ticketMapper::toResponse).toList();
    }


    /**
     * Lấy QR code cho BOOKING - KH nhận trong email
     * 1 booking = 1 QR duy nhất để check-in
     */
    @Transactional(readOnly = true)
    public String getBookingQR(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // Kiểm tra quyền: chỉ chủ booking mới xem được
        if (!booking.getUser().getId().equals(getCurrentUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Chỉ booking PENDING hoặc CONFIRMED mới có QR hợp lệ
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new AppException(ErrorCode.BOOKING_CANCELLED);
        }

        // QR chỉ chứa bookingCode
        return QRCodeUtil.generateBase64QR(bookingCode);
    }

    // ─────────────────────────────────────────────────────────────────
    // CHECK-IN — nhân viên scan QR tại cửa rạp
    // ─────────────────────────────────────────────────────────────────

    @Transactional
    public CheckInResponse checkIn(CheckInRequest request) {
        Ticket ticket = getTicketOrThrow(request.ticketCode());

        // 1. Vé phải VALID
        if (ticket.getStatus() != TicketStatus.VALID) {
            throw new AppException(ErrorCode.TICKET_NOT_VALID);
        }

        // 2. Suất chiếu phải đang ONGOING
        var showtime = ticket.getBooking().getShowtime();
        if (showtime.getStatus() != ShowTimeStatus.ONGOING) {
            throw new AppException(ErrorCode.SHOWTIME_NOT_ONGOING);
        }

        // 3. Check-in
        ticket.setStatus(TicketStatus.USED);
        ticket.setCheckedInAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        log.info("CheckIn success: ticketCode={}, showtime={}, seat={}{}",
            ticket.getTicketCode(),
            showtime.getId(),
            ticket.getSeat().getSeatRow(),
            ticket.getSeat().getSeatNumber());

        return new CheckInResponse(
            ticket.getTicketCode(),
            showtime.getMovie().getTitle(),
            showtime.getRoom().getName(),
            ticket.getSeat().getSeatRow(),
            ticket.getSeat().getSeatNumber(),
            ticket.getStatus(),
            ticket.getCheckedInAt(),
            "Check-in thành công! Chúc xem phim vui vẻ 🎬"
        );
    }

    @Transactional
    public List<CheckInResponse> checkInByBookingCode(String bookingCode) {
        List<Ticket> tickets = ticketRepository.findAllByBookingCode(bookingCode);

        if (tickets.isEmpty()) {
            throw new AppException(ErrorCode.TICKET_NOT_FOUND);
        }

        var showtime = tickets.get(0).getBooking().getShowtime();
        if (showtime.getStatus() != ShowTimeStatus.ONGOING) {
            throw new AppException(ErrorCode.SHOWTIME_NOT_ONGOING);
        }

        return tickets.stream().map(t -> {
                    if (t.getStatus() != TicketStatus.VALID) return null;
                    t.setStatus(TicketStatus.USED);
                    t.setCheckedInAt(LocalDateTime.now());
                    return t;
                })
                .filter(Objects::nonNull)
                .peek(ticketRepository::save)
                .map(t -> new CheckInResponse(
                        t.getTicketCode(),
                        showtime.getMovie().getTitle(),
                        showtime.getRoom().getName(),
                        t.getSeat().getSeatRow(),
                        t.getSeat().getSeatNumber(),
                        t.getStatus(),
                        t.getCheckedInAt(),
                        "Check-in thành công!"
                ))
                .toList();
    }

    // ─────────────────────────────────────────────────────────────────
    // SCHEDULED — expire vé sau khi suất chiếu kết thúc
    // ─────────────────────────────────────────────────────────────────

    @Scheduled(cron = "0 0 * * * *")   // mỗi giờ
    @Transactional
    public void expireUnusedTickets() {
        List<Ticket> tickets = ticketRepository.findValidTicketsOfFinishedShowtimes();
        if (tickets.isEmpty()) return;

        tickets.forEach(t -> t.setStatus(TicketStatus.EXPIRED));
        ticketRepository.saveAll(tickets);

        log.info("Expired {} unused ticket(s)", tickets.size());
    }

    // ─────────────────────────────────────────────────────────────────
    // PRIVATE
    // ─────────────────────────────────────────────────────────────────

    private Ticket getTicketOrThrow(String ticketCode) {
        return ticketRepository.findByTicketCodeWithDetails(ticketCode)
            .orElseThrow(() -> new AppException(ErrorCode.TICKET_NOT_FOUND));
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}