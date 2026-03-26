package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Response.Booking.BookingResponse;
import org.example.cinemaBooking.Dto.Response.Booking.BookingSummaryResponse;
import org.example.cinemaBooking.Entity.Booking;
import org.example.cinemaBooking.Entity.Showtime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

// BookingMapper.java
@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "bookingId",     source = "id")
    @Mapping(target = "showtime",      expression = "java(mapShowtime(b))")
    @Mapping(target = "tickets",       expression = "java(mapTickets(b))")
    @Mapping(target = "products",      expression = "java(mapProducts(b))")
    @Mapping(target = "paymentUrl",    ignore = true)   // service inject sau
    BookingResponse toResponse(Booking b);

    @Mapping(target = "bookingId",   source = "id")
    @Mapping(target = "movieTitle",  source = "showtime.movie.title")
    @Mapping(target = "startTime",   source = "showtime.startTime")
    @Mapping(target = "seatCount",   expression = "java(b.getTickets().size())")
    BookingSummaryResponse toSummary(Booking b);

    // ── helpers ──────────────────────────────────────────────────────

    default BookingResponse.ShowtimeInfo mapShowtime(Booking b) {
        Showtime s = b.getShowtime();
        return new BookingResponse.ShowtimeInfo(
            s.getId(), s.getMovie().getTitle(),
            s.getRoom().getName(), s.getRoom().getCinema().getName(),
            s.getStartTime()
        );
    }

    default List<BookingResponse.TicketInfo> mapTickets(Booking b) {
        return b.getTickets().stream().map(t -> new BookingResponse.TicketInfo(
            t.getTicketCode(),
            t.getSeat().getSeatRow(),
            t.getSeat().getSeatNumber(),
            t.getSeat().getSeatType().getName(),
            t.getPrice(),
            t.getStatus()
        )).toList();
    }

    default List<BookingResponse.ProductInfo> mapProducts(Booking b) {
        return b.getBookingProducts().stream().map(p -> new BookingResponse.ProductInfo(
            p.getItemId(), p.getItemName(), p.getItemType(),
            p.getItemPrice(), p.getQuantity(),
            p.getItemPrice().multiply(BigDecimal.valueOf(p.getQuantity()))
        )).toList();
    }
}