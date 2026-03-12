package org.example.cinemaBooking.Mapper;

import org.example.cinemaBooking.Entity.UserEntity;
import org.example.cinemaBooking.Model.Request.RegisterRequest;
import org.example.cinemaBooking.Model.Response.UserInfoResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserInfoResponse toUserInfoResponse(UserEntity userEntity);

    UserEntity toUserEntity(RegisterRequest registerRequest);
}
