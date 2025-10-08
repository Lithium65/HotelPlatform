package com.example.hotelservice.services.impl;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.domain.Reservation;
import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.repos.RoomTypeRepo;
import com.example.hotelservice.services.*;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    @Autowired
    private final RoomTypeRepo roomTypeRepo;

    @Autowired
    private FileService fileService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private HotelService hotelService;

    public RoomTypeServiceImpl(RoomTypeRepo roomTypeRepo) {
        this.roomTypeRepo = roomTypeRepo;
    }

    @Override
    public Model getHotelRoomsModel(Long id, Model model, int page, int size, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RoomType> roomTypesPage = getHotelRoomTypes(id, pageable);
        try {
            model.addAttribute("hotelName", hotelService.getHotelById(id).orElseThrow(() -> new NotFoundException("Hotel not found")).getHotelName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("roomTypes", roomTypesPage.getContent());
        model.addAttribute("hotelId", id);
        model.addAttribute("currentPage", roomTypesPage.getNumber());
        model.addAttribute("totalPages", roomTypesPage.getTotalPages() > 0 ? roomTypesPage.getTotalPages() : 0);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return model;
    }

    @Override
    public RoomType getRoomTypeById(Long id) {
        return roomTypeRepo.findById(id).orElseThrow(() -> new RuntimeException("Тип комнаты не найден"));
    }

    @Override
    public Hotel getHotelByRoomType(RoomType roomType) {
        return roomType.getHotel();
    }

    @Override
    public void deleteHotelRoomTypes(long hotelId) {
        List<RoomType> roomTypes = roomTypeRepo.findByHotelId(hotelId);
        for (RoomType roomType : roomTypes) {
            roomService.deleteByRoomType(roomType);
        }
        roomTypeRepo.deleteAll(roomTypes);
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepo.findAll();
    }

    @Override
    public Page<RoomType> getAllRoomTypes(Pageable pageable) {
        return roomTypeRepo.findAll(pageable);
    }

    @Override
    public List<RoomType> getHotelRoomTypes(Long hotel_id) {
        return roomTypeRepo.findByHotelId(hotel_id);
    }

    @Override
    public void createRoomType(RoomType roomType) {
        roomTypeRepo.save(roomType);
    }

    @Override
    public void deleteRoomType(Long id) {
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
                if (baby) {
                    if (!(Objects.equals(getHotelByRoomType(roomType).getCity(), city) && roomType.getPlaces() == numberOfPeople && roomType.isBaby() == baby)) {
                        continue;
                    }
                } else if (!(Objects.equals(getHotelByRoomType(roomType).getCity(), city) && roomType.getPlaces() == numberOfPeople)) {
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

            Iterable<Room> rooms = roomService.findByRoomType(roomType);
            boolean isRoomAvailable = false;

            for (Room room : rooms) {
                List<Reservation> roomConflictingReservations = reservationService.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(room, checkOutDate, checkInDate);

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

