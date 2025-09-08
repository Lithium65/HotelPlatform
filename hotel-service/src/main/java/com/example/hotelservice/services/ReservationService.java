package com.example.hotelservice.services;

import com.example.hotelservice.domain.*;
import com.example.hotelservice.repos.HotelRepo;
import com.example.hotelservice.repos.ReservationRepo;
import com.example.hotelservice.repos.RoomRepo;
import com.example.hotelservice.repos.RoomTypeRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepo reservationRepo;
    private final RoomRepo roomRepo;
    private final HotelRepo hotelRepo;
    private final RoomTypeRepo roomTypeRepo;

    public ReservationService(ReservationRepo reservationRepo, RoomRepo roomRepo, HotelRepo hotelRepo, RoomTypeRepo roomTypeRepo) {
        this.reservationRepo = reservationRepo;
        this.roomRepo = roomRepo;
        this.hotelRepo = hotelRepo;
        this.roomTypeRepo = roomTypeRepo;
    }

    public List<Reservation> getAllReservations() {
        return (List<Reservation>) reservationRepo.findAll();
    }

    public Page<Reservation> getAllReservations(Pageable pageable) {
        return reservationRepo.findAll(pageable);
    }

    public Iterable<Reservation> getReservationsByUser(User user) { return reservationRepo.findByUser(user); }

    public Optional<Reservation> getReservationById(long id) { return reservationRepo.findById(id); }

    public void createReservation(Reservation reservation, RoomType roomType) {
        List<Room> rooms = roomRepo.findByRoomType(roomType);
        LocalDate checkIn = reservation.getCheckIn();
        LocalDate checkOut = reservation.getCheckOut();

        if(checkIn.isAfter(checkOut))
        {
            throw new RuntimeException("Выбраны неверные даты");
        }

        List<Reservation> conflictingReservations = new ArrayList<>();
        Room room = null;

        for (int i = 0; i < rooms.size(); i++) {
            List<Reservation> roomConflictingReservations = reservationRepo.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(rooms.get(i), checkOut, checkIn);
            if (roomConflictingReservations.isEmpty()){
                room = rooms.get(i);
                break;
            }
            if (i == rooms.size() - 1 && conflictingReservations != null) {
                conflictingReservations.addAll(roomConflictingReservations);
            }
        }

        if (conflictingReservations.isEmpty()) {
            reservation.setRoom(room);
            reservationRepo.save(reservation);
        } else {
            throw new RuntimeException("Нет доступных комнат заданного типа в выбранные даты");
        }

    }

    public Page<Reservation> getReservationsByHotelName(String hotelName, Pageable pageable) {
        return reservationRepo.findByHotelName(hotelName, pageable);
    }

    public void deleteReservation(Long id){
        reservationRepo.deleteById(id);
    }

    public List<Reservation> findByRoom(Optional<Room> room) { return reservationRepo.findByRoom(room); }

    public List<Reservation> findByRoomType(RoomType filter) { return reservationRepo.findByRoomRoomType(filter); }

    public List<Reservation> findConflictingReservations(Room room, LocalDate checkOutDate, LocalDate checkInDate) { return reservationRepo.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(room, checkOutDate, checkInDate); }
}


