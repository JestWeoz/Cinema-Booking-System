package org.example.cinemaBooking.Service.Booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.Booking.CreateBookingRequest;
import org.example.cinemaBooking.Dto.Response.Booking.BookingResponse;
import org.example.cinemaBooking.Dto.Response.Booking.BookingSummaryResponse;
import org.example.cinemaBooking.Entity.*;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.BookingMapper;
import org.example.cinemaBooking.Repository.*;
import org.example.cinemaBooking.Service.Promotion.PromotionService;
import org.example.cinemaBooking.Service.Showtime.ShowTImeSeatService;
import org.example.cinemaBooking.Shared.utils.BookingStatus;
import org.example.cinemaBooking.Shared.utils.ItemType;
import org.example.cinemaBooking.Shared.utils.SeatStatus;
import org.example.cinemaBooking.Shared.utils.TicketStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {
    BookingRepository bookingRepository;
    TicketRepository ticketRepository;
    ShowtimeSeatRepository showtimeSeatRepository;
    ShowtimeRepository showtimeRepository;
    PromotionRepository promotionRepository;
    ProductRepository productRepository;
    ComboRepository comboRepository;
    BookingMapper bookingMapper;
    UserRepository userRepository;
    ShowTImeSeatService showtimeSeatService;
    PromotionService promotionService;

    private static final int BOOKING_EXPIRY_MINUTES = 10;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        String userId = getCurrentUser().getId();

        Showtime showtime = showtimeRepository.findByIdWithDetails(request.showtimeId())
                .orElseThrow(() -> new AppException(ErrorCode.SHOWTIME_NOT_FOUND));

        if (!showtime.isBookable()) {
            throw new AppException(ErrorCode.SHOWTIME_STATE_INVALID);
        }

        List<ShowtimeSeat> lockedSeats = showtimeSeatRepository
                .findByShowtimeIdAndSeatIds(request.showtimeId(), request.seatIds());

        if (lockedSeats.size() != request.seatIds().size()) {
            throw new AppException(ErrorCode.SEAT_NOT_FOUND);
        }
        lockedSeats.forEach(ss -> {
            if (ss.getStatus() != SeatStatus.LOCKED) {
                throw new AppException(ErrorCode.SEAT_NOT_LOCKED);
            }
            if (userId.equals(ss.getLockedByUser())) {
                throw new AppException(ErrorCode.SEAT_LOCK_FORBIDDEN);
            }
        });

        BigDecimal totalTicketPrice = lockedSeats.stream()
                .map(ss -> showtime.getBasePrice()
                        .add(ss.getSeat().getSeatType().getPriceModifier()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Tính giá đồ ăn/combo
        List<BookingProduct> bookingProducts = resolveProducts(request.products());
        BigDecimal totalProductPrice = bookingProducts.stream()
                .map(p -> p.getItemPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPrice = totalTicketPrice.add(totalProductPrice);


        // 5. Áp promotion (nếu có)
        BigDecimal discountAmount = BigDecimal.ZERO;
        Promotion promotion = null;
        if (request.promotionCode() != null) {
            var preview = promotionService.previewPromotion(
                    request.promotionCode(), userId, totalPrice);

            promotion = promotionRepository
                    .findActiveByCode(request.promotionCode(), LocalDate.now())
                    .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

            discountAmount = preview.discountAmount();
        }

        BigDecimal finalPrice = totalPrice.subtract(discountAmount)
                .max(BigDecimal.ZERO);

        // 6. Tạo Booking
        Booking booking = Booking.builder()
                .bookingCode(generateBookingCode())
                .user(getUserRef(userId))
                .showtime(showtime)
                .promotion(promotion)
                .totalPrice(totalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .expiredAt(LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES))
                .build();

        // 7. Tạo Ticket cho từng ghế
        List<Ticket> tickets = lockedSeats.stream()
                .map(ss -> Ticket.builder()
                        .ticketCode(generateTicketCode())
                        .booking(booking)
                        .seat(ss.getSeat())
                        .price(showtime.getBasePrice()
                                .add(ss.getSeat().getSeatType().getPriceModifier()))
                        .status(TicketStatus.VALID)
                        .build())
                .toList();


        // 8. Gán booking vào BookingProduct
        bookingProducts.forEach(p -> p.setBooking(booking));

        booking.getTickets().addAll(tickets);
        booking.getBookingProducts().addAll(bookingProducts);

        Booking saved = bookingRepository.save(booking);

        log.info("Booking created: code={}, user={}, showtime={}, seats={}, finalPrice={}",
                saved.getBookingCode(), userId, request.showtimeId(),
                request.seatIds().size(), finalPrice);

        BookingResponse response = bookingMapper.toResponse(saved);
        // paymentUrl sẽ được PaymentService inject — trả về bookingId cho FE tạo payment
        return response;
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(String bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        checkOwnership(booking);
        return bookingMapper.toResponse(booking);
    }


    @Transactional(readOnly = true)
    public List<BookingSummaryResponse> getMyBookings() {
        String userId = getCurrentUser().getId();
        return bookingRepository.findAllByUserId(userId)
                .stream().map(bookingMapper::toSummary).toList();
    }

    @Transactional
    public void confirmBooking(String bookingId) {
        Booking booking = getBookingOrThrow(bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new AppException(ErrorCode.BOOKING_STATUS_INVALID);
        }
        if (booking.isExpired()) {
            throw new AppException(ErrorCode.BOOKING_EXPIRED);
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setExpiredAt(null);

        // Chuyển ghế LOCKED → BOOKED
        List<String> seatIds = booking.getTickets().stream()
                .map(t -> t.getSeat().getId()).toList();

        showtimeSeatService.confirmBooking(
                booking.getShowtime().getId(),
                seatIds,
                booking.getUser().getId()
        );

        bookingRepository.save(booking);
        log.info("Booking confirmed: code={}", booking.getBookingCode());
    }



    @Transactional
    public BookingResponse cancelBooking(String bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        checkOwnership(booking);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new AppException(ErrorCode.BOOKING_STATUS_INVALID);
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new AppException(ErrorCode.BOOKING_ALREADY_CONFIRMED);
        }

        booking.setStatus(BookingStatus.CANCELLED);

        // Giải phóng ghế về AVAILABLE
        List<String> seatIds = booking.getTickets().stream()
                .map(t -> t.getSeat().getId()).toList();

        showtimeSeatService.releaseBookedSeats(
                booking.getShowtime().getId(), seatIds);

        Booking saved = bookingRepository.save(booking);
        log.warn("Booking cancelled by user: code={}", booking.getBookingCode());
        return bookingMapper.toResponse(saved);
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void expireStaleBookings() {
        List<Booking> expired = bookingRepository
                .findExpiredPendingBookings(LocalDateTime.now());

        if (expired.isEmpty()) return;

        expired.forEach(b -> {
            b.setStatus(BookingStatus.CANCELLED);

            List<String> seatIds = b.getTickets().stream()
                    .map(t -> t.getSeat().getId()).toList();

            showtimeSeatService.releaseBookedSeats(
                    b.getShowtime().getId(), seatIds);
        });

        bookingRepository.saveAll(expired);
        log.info("Expired {} stale booking(s)", expired.size());
    }


    //Internal   method, called by scheduled job to cancel expired pending bookings
    private UserEntity getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return userRepository.findUserEntityByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private List<BookingProduct> resolveProducts(
            List<CreateBookingRequest.BookingProductItem> items) {
        return items.stream().map(item -> {
            String name;
            BigDecimal price;
            if (item.itemType() == ItemType.PRODUCT) {
                Product p = productRepository.findById(item.itemId())
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
                name = p.getName(); price = p.getPrice();
            } else {
                Combo c = comboRepository.findById(item.itemId())
                        .orElseThrow(() -> new AppException(ErrorCode.COMBO_NOT_FOUND));
                name = c.getName(); price = c.getPrice();
            }
            return BookingProduct.builder()
                    .itemType(item.itemType())
                    .itemId(item.itemId())
                    .itemName(name).itemPrice(price)
                    .quantity(item.quantity())
                    .build();
        }).toList();
    }

    private String generateBookingCode() {
        String code;
        do {
            code = "BK" + System.currentTimeMillis() % 1_000_000
                    + (char)('A' + (int)(Math.random() * 26));
        } while (bookingRepository.existsByBookingCode(code));
        return code;
    }

    private String generateTicketCode() {
        return "TK" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private Booking getBookingOrThrow(String id) {
        return bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }

    private void checkOwnership(Booking booking) {
        if (!booking.getUser().getId().equals(getCurrentUser().getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private UserEntity getUserRef(String userId) {
        // Dùng getById để tránh query — chỉ cần reference FK
        return userRepository.getReferenceById(userId);
    }

}
