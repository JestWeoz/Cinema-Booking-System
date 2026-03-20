package org.example.cinemaBooking.Dto.Request;


import java.util.List;

public record AddPeopleToMovieRequest(
        List<PeopleRoleRequest> people
){
}