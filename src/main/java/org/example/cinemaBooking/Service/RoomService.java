package org.example.cinemaBooking.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.cinemaBooking.Dto.Request.CreateRoomRequest;
import org.example.cinemaBooking.Dto.Response.RoomResponse;
import org.example.cinemaBooking.Entity.Cinema;
import org.example.cinemaBooking.Entity.Room;
import org.example.cinemaBooking.Exception.AppException;
import org.example.cinemaBooking.Exception.ErrorCode;
import org.example.cinemaBooking.Mapper.RoomMapper;
import org.example.cinemaBooking.Repository.CinemaRepository;
import org.example.cinemaBooking.Repository.RoomRepository;
import org.example.cinemaBooking.Shared.response.PageResponse;
import org.example.cinemaBooking.Shared.utils.Status;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(
        level = lombok.AccessLevel.PRIVATE,
        makeFinal = true
)
public class RoomService
{
    RoomMapper roomMapper;
    RoomRepository roomRepository;
    CinemaRepository cinemaRepository;

    public RoomResponse createRoom(CreateRoomRequest request){
        Room room = roomMapper.toRoomEntity(request);
        room.setCinema(cinemaRepository.findCinemaById(request.cinemaId()));
        room.setStatus(Status.INACTIVE);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    public void deleteRoomByID(String roomId){
        Optional<Room> exitingRoom = roomRepository.findById(roomId);
        if(exitingRoom.isPresent()){
            if(exitingRoom.get().getStatus() == Status.INACTIVE){
                throw new AppException(ErrorCode.ROOM_ALREADY_INACTIVE);
            }
            roomRepository.delete(exitingRoom.get());
        } else{
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }
    }

    public RoomResponse getRoomByID(String roomId){
        Optional<Room> exitingRoom = roomRepository.findById(roomId);
        if(exitingRoom.isPresent()){
            return roomMapper.toResponse(exitingRoom.get());
        } else{
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }
    }
    public void toggleRoomStatus(String roomId){
        Optional<Room> exitingRoom = roomRepository.findById(roomId);
        if(exitingRoom.isPresent()) {
            if (exitingRoom.get().getStatus() == Status.ACTIVE) {
                exitingRoom.get().setStatus(Status.INACTIVE);
            } else {
                exitingRoom.get().setStatus(Status.ACTIVE);
            }
        } else {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }
    }
    public PageResponse<RoomResponse> getAllRooms(){

    }

}
