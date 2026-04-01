package org.example.cinemaBooking.DTO.Response.Statistics;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
    BigDecimal revenueToday,
    Integer ticketsToday,
    Integer usersToday,
    Integer showtimeToday){

}