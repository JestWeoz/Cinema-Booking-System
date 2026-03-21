package org.example.cinemaBooking.Dto.Request;


import org.example.cinemaBooking.Dto.Request.Movie.PeopleRoleRequest;

import java.util.List;

public record AddPeopleToMovieRequest(
        List<PeopleRoleRequest> people
){
}