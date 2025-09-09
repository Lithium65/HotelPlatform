package com.example.hotelservice.services.impl;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.domain.Reservation;
import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.repos.ReservationRepo;
import com.example.hotelservice.repos.RoomRepo;
import com.example.hotelservice.repos.RoomTypeRepo;
import com.example.hotelservice.services.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    @Autowired
    private final RoomTypeRepo roomTypeRepo;
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private ReservationRepo reservationRepo;

    public RoomTypeServiceImpl(RoomTypeRepo roomTypeRepo) {
        this.roomTypeRepo = roomTypeRepo;
    }

    @Override
    public RoomType getRoomTypeById(Long id) {
        return roomTypeRepo.findById(id).orElseThrow(() -> new RuntimeException("Тип комнаты не найден"));
    }

    @Override
    public Hotel getHotelByRoomType(RoomType roomType){
        return roomType.getHotel();
    }

    @Override
    public void deleteHotelRoomTypes(long hotelId){
        List<RoomType> roomTypes = roomTypeRepo.findByHotelId(hotelId);
        for (RoomType roomType : roomTypes) {
            roomRepo.deleteByRoomType(roomType);
        }
        roomTypeRepo.deleteAll(roomTypes);
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepo.findAll();
    }

    @Override
    public Page<RoomType> getAllRoomTypes(Pageable pageable) { return roomTypeRepo.findAll(pageable); }

    @Override
    public List<RoomType> getHotelRoomTypes(Long hotel_id) {
        return roomTypeRepo.findByHotelId(hotel_id);
    }

    @Override
    public void createRoomType(RoomType roomType) {
        roomTypeRepo.save(roomType);
    }

    @Override
    public void deleteRoomType(Long id){
        roomTypeRepo.deleteById(id);
    }

    @Override
    public void updateRoomType(RoomType roomType) {
        RoomType existingRoomType = roomTypeRepo.findById(roomType.getId()).orElse(null);
        if (existingRoomType != null) {
            existingRoomType.setName(roomType.getName());
            existingRoomType.setDescription(roomType.getDescription());
            existingRoomType.setPrice(roomType.getPrice());
            existingRoomType.setFilename1(roomType.getFilename1());
            existingRoomType.setFilename2(roomType.getFilename2());
            existingRoomType.setFilename3(roomType.getFilename3());
            existingRoomType.setId(roomType.getId());
            roomTypeRepo.save(existingRoomType);
        }
    }

    @Override
    public Page<RoomType> getHotelRoomTypes(Long hotelId, Pageable pageable) {
        return roomTypeRepo.findByHotelId(hotelId, pageable);
    }

    @Override
    public ArrayList<RoomType> getAvailableRooms(String city, int numberOfPeople, boolean baby, LocalDate checkInDate, LocalDate checkOutDate) {
        ArrayList<RoomType> availableRoomTypes = new ArrayList<>();
        List<RoomType> roomTypes = roomTypeRepo.findAll();

        for (RoomType roomType : roomTypes) {
            if (numberOfPeople < 5) {
                if(baby) {
                    if (!(Objects.equals(getHotelByRoomType(roomType).getCity(), city) && roomType.getPlaces() == numberOfPeople && roomType.isBaby() == baby)) {
                        continue;
                    }
                }
                else if (!(Objects.equals(getHotelByRoomType(roomType).getCity(), city) && roomType.getPlaces() == numberOfPeople)) {
                    continue;
                }
            } else {
                if (baby) {
                    if (!(Objects.equals(getHotelByRoomType(roomType).getCity(), city) && roomType.getPlaces() >= 5 && roomType.isBaby() == baby)) {
                        continue;
                    }
                } else {
                    if (!(Objects.equals(getHotelByRoomType(roomType).getCity(), city) && roomType.getPlaces() >= 5)) {
                        continue;
                    }
                }
            }

            List<Room> rooms = roomRepo.findByRoomType(roomType);
            boolean isRoomAvailable = false;

            for (Room room : rooms) {
                List<Reservation> roomConflictingReservations = reservationRepo.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(room, checkOutDate, checkInDate);

                if (roomConflictingReservations == null || roomConflictingReservations.isEmpty()) {
                    isRoomAvailable = true;
                    break;
                }
            }

            if (isRoomAvailable) {
                availableRoomTypes.add(roomType);
            }
        }

        return availableRoomTypes;
    }
}

