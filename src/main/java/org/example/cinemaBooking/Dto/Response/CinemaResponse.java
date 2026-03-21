package org.example.cinemaBooking.Dto.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.cinemaBooking.Shared.utils.Status;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CinemaResponse {
    String id;
    String name;
    String address;
    String phone;
    String hotline;
    String logoUrl;
    Status status;

    Set<RoomResponse> rooms; // nếu muốn trả kèm phòng
}