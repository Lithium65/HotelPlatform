package com.example.hotelservice.services;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.domain.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface RoomTypeService {
    RoomType getRoomTypeById(Long id);

    Hotel getHotelByRoomType(RoomType roomType);

    void deleteHotelRoomTypes(long hotelId);

    List<RoomType> getAllRoomTypes();

    Page<RoomType> getAllRoomTypes(Pageable pageable);

    List<RoomType> getHotelRoomTypes(Long hotel_id);

    void createRoomType(RoomType roomType);

    void deleteRoomType(Long id);

    void updateRoomType(RoomType roomType);

    Page<RoomType> getHotelRoomTypes(Long hotelId, Pageable pageable);

    ArrayList<RoomType> getAvailableRooms(String city, int numberOfPeople, boolean baby, LocalDate checkInDate, LocalDate checkOutDate);
}
