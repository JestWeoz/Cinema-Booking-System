package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Dto.Request.CreateRoomRequest;
import org.example.cinemaBooking.Dto.Request.RegisterRequest;
import org.example.cinemaBooking.Dto.Request.UpdateProfileRequest;
import org.example.cinemaBooking.Dto.Request.UpdateRoomRequest;
import org.example.cinemaBooking.Dto.Response.RoomResponse;
import org.example.cinemaBooking.Entity.Room;
import org.example.cinemaBooking.Entity.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "Spring")
public interface RoomMapper {

    // ✅ ENTITY -> RESPONSE
    @Mapping(source = "cinema.id", target = "cinemaId")
    @Mapping(source = "cinema.name", target = "cinemaName")
    RoomResponse toResponse(Room room);

    // ✅ REQUEST -> ENTITY
    @Mapping(target = "cinema", ignore = true) // xử lý ở service
    @Mapping(target = "seats", ignore = true)
    Room toRoomEntity(CreateRoomRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRoom(UpdateRoomRequest request, @MappingTarget Room room);

}
