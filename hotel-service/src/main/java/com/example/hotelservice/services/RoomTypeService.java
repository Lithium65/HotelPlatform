package com.example.hotelservice.services;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.domain.Reservation;
import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.repos.HotelRepo;
import com.example.hotelservice.repos.ReservationRepo;
import com.example.hotelservice.repos.RoomRepo;
import com.example.hotelservice.repos.RoomTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RoomTypeService {

    @Autowired
    private final RoomTypeRepo roomTypeRepo;
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private ReservationRepo reservationRepo;

    public RoomTypeService(RoomTypeRepo roomTypeRepo) {
        this.roomTypeRepo = roomTypeRepo;
    }

    public RoomType getRoomTypeById(Long id) {
        return roomTypeRepo.findById(id).orElseThrow(() -> new RuntimeException("Тип комнаты не найден"));
    }

    public Hotel getHotelByRoomType(RoomType roomType){
        return roomType.getHotel();
    }

    public void deleteHotelRoomTypes(long hotelId){
        List<RoomType> roomTypes = roomTypeRepo.findByHotelId(hotelId);
        for (RoomType roomType : roomTypes) {
            roomRepo.deleteByRoomType(roomType);
        }
        roomTypeRepo.deleteAll(roomTypes);
    }

    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepo.findAll();
    }

    public Page<RoomType> getAllRoomTypes(Pageable pageable) { return roomTypeRepo.findAll(pageable); }

    public List<RoomType> getHotelRoomTypes(Long hotel_id) {
        return roomTypeRepo.findByHotelId(hotel_id);
    }

    public void createRoomType(RoomType roomType) {
        roomTypeRepo.save(roomType);
    }

    public void deleteRoomType(Long id){
        roomTypeRepo.deleteById(id);
    }

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

    public Page<RoomType> getHotelRoomTypes(Long hotelId, Pageable pageable) {
        return roomTypeRepo.findByHotelId(hotelId, pageable);
    }

    public ArrayList<RoomType> getAvailableRooms(String city, int numberOfPeople, boolean baby, LocalDate checkInDate, LocalDate checkOutDate) {
        ArrayList<RoomType> availableRoomTypes = new ArrayList<>();
        List<RoomType> roomTypes = roomTypeRepo.findAll();

        for (RoomType roomType : roomTypes) {
            if (numberOfPeople < 5) {
                if(baby) { //Если требуется место для ребенка, идёт поиск ТОЛЬКО по номерам с детскими местами
                    if (!(Objects.equals(getHotelByRoomType(roomType).getCity(), city) && roomType.getPlaces() == numberOfPeople && roomType.isBaby() == baby)) {
                        continue;
                    }
                } //Если не требуется, не проверяем
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

            List<Room> rooms = (List<Room>) roomRepo.findByRoomType(roomType);
            boolean isRoomAvailable = false;

            for (Room room : rooms) {
                // Проверяем, есть ли конфликтующие бронирования для данной комнаты
                List<Reservation> roomConflictingReservations = reservationRepo.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(room, checkOutDate, checkInDate);

                // Если конфликтующих бронирований нет, комната доступна
                if (roomConflictingReservations == null || roomConflictingReservations.isEmpty()) {
                    isRoomAvailable = true;
                    break;
                }
            }

            // Если хотя бы одна комната доступна, добавляем тип комнаты в список доступных
            if (isRoomAvailable) {
                availableRoomTypes.add(roomType);
            }
        }

        return availableRoomTypes;
    }

    public Page<RoomType> findAll(Pageable pageable) { return roomTypeRepo.findAll(pageable); }
}

