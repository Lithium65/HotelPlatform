package com.example.hotelservice.services;

import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.repos.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService{

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private RoomRepo roomRepo;

    public Iterable<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    public void deleteByRoomType(RoomType roomType) {
        List<Room> HotelRooms = roomRepo.findByRoomType(roomType);
        roomRepo.deleteAll(HotelRooms);
    }

    public List<Room> getAllHotelRooms(long hotelId){
        List<Room> hotelRooms = new ArrayList<>();
        List<RoomType> hotelRoomTypes = roomTypeService.getHotelRoomTypes(hotelId);
        for (RoomType hotelRoomType : hotelRoomTypes) {
            hotelRooms.addAll(roomRepo.findByRoomType(hotelRoomType));
        }
        return hotelRooms;
    }

    public Iterable<Room> findByRoomType(RoomType filter) { return roomRepo.findByRoomType(filter); }

    public Boolean checkRoomExistence(Room room) {
        return roomRepo.findByNumberAndRoomType(room.getNumber(), room.getRoom_type()).isPresent();
    }

    public void save(Room room) { roomRepo.save(room); }

    public Iterable<Room> findAll() { return roomRepo.findAll(); }

    public Optional<Room> findById(Long id) { return roomRepo.findById(id); }

    public void delete(Room room) { roomRepo.delete(room); }
}


