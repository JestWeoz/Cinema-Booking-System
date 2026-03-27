package org.example.cinemaBooking.Dto.Request.Movie;


import java.util.List;

public record AddPeopleToMovieRequest(
        List<PeopleRoleRequest> people
){
}