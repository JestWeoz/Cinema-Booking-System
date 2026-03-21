package org.example.cinemaBooking.Dto.Response.Movie;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieImageResponse {

    String id;
    String imageUrl;

}