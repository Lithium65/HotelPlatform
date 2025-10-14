package com.example.hotelservice.services;

import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    Iterable<Room> getAllRooms();

    void deleteByRoomType(RoomType roomType);

    List<Room> getAllHotelRooms(long hotelId);

    Iterable<Room> findByRoomType(RoomType filter);

    Boolean checkRoomExistence(Room room);

    void save(Room room);

    Optional<Room> findById(Long id);

    void delete(Room room);
}
