package org.example.cinemaBooking.Model.Response;

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