package com.example.hotelservice.services.impl;

import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.repos.RoomRepo;
import com.example.hotelservice.repos.RoomTypeRepo;
import com.example.hotelservice.services.RoomService;
import com.example.hotelservice.services.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private RoomTypeRepo roomTypeRepo;

    @Override
    public Iterable<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    @Override
    public void deleteByRoomType(RoomType roomType) {
        List<Room> HotelRooms = roomRepo.findByRoomType(roomType);
        roomRepo.deleteAll(HotelRooms);
    }

    @Override
    public List<Room> getAllHotelRooms(long hotelId){
        List<Room> hotelRooms = new ArrayList<>();
        List<RoomType> hotelRoomTypes = roomTypeRepo.findByHotelId(hotelId);
        for (RoomType hotelRoomType : hotelRoomTypes) {
            hotelRooms.addAll(roomRepo.findByRoomType(hotelRoomType));
        }
        return hotelRooms;
    }

    @Override
    public Iterable<Room> findByRoomType(RoomType filter) { return roomRepo.findByRoomType(filter); }

    @Override
    public Boolean checkRoomExistence(Room room) {
        return roomRepo.findByNumberAndRoomType(room.getNumber(), room.getRoom_type()).isPresent();
    }

    @Override
    public void save(Room room) { roomRepo.save(room); }

    @Override
    public Optional<Room> findById(Long id) { return roomRepo.findById(id); }

    @Override
    public void delete(Room room) { roomRepo.delete(room); }
}


