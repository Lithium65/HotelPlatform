package com.example.hotelservice.services.impl;

import com.example.hotelservice.domain.*;
import com.example.hotelservice.repos.ReservationRepo;
import com.example.hotelservice.repos.RoomRepo;
import com.example.hotelservice.services.ReservationService;
import javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepo reservationRepo;
    private final RoomRepo roomRepo;

    public ReservationServiceImpl(ReservationRepo reservationRepo, RoomRepo roomRepo) {
        this.reservationRepo = reservationRepo;
        this.roomRepo = roomRepo;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return (List<Reservation>) reservationRepo.findAll();
    }

    @Override
    public Page<Reservation> getAllReservations(Pageable pageable) {
        return reservationRepo.findAll(pageable);
    }

    @Override
    public Iterable<Reservation> getReservationsByUser(User user) {
        return reservationRepo.findByUser(user);
    }

    @Override
    public Optional<Reservation> getReservationById(long id) {
        return reservationRepo.findById(id);
    }

    @Override
    public Page<Reservation> findByHotelId(@Param("hotelId") Long hotelId, Pageable pageable) {
        return reservationRepo.findByHotelId(hotelId, pageable);
    }

    ;

    @Override
    public List<Reservation> findByHotelId(Long hotelId) {
        return reservationRepo.findByHotelId(hotelId);
    }

    @Override
    public void createReservation(Reservation reservation,
                                  RoomType roomType) {
        List<Room> rooms = roomRepo.findByRoomType(roomType);
        LocalDate checkIn = reservation.getCheckIn();
        LocalDate checkOut = reservation.getCheckOut();

        if (checkIn.isAfter(checkOut)) {
            throw new RuntimeException("Выбраны неверные даты");
        }

        List<Reservation> conflictingReservations = new ArrayList<>();
        Room room = null;

        for (int i = 0; i < rooms.size(); i++) {
            List<Reservation> roomConflictingReservations = reservationRepo.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(rooms.get(i), checkOut, checkIn);
            if (roomConflictingReservations.isEmpty()) {
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

    @Override
    public List<Reservation> findFilteredReservationsByRoomTypeAndDate(Long roomTypeId, LocalDate date, Boolean isCheckOut) {
        if (isCheckOut) return reservationRepo.findByRoomAndCheckOut(roomTypeId, date);
        else return reservationRepo.findByRoomAndCheckIn(roomTypeId, date);
    }

    @Override
    public List<Reservation> findFilteredReservationsByDate(Long hotelId, LocalDate date, Boolean isCheckOut) {
        if (isCheckOut) return reservationRepo.findByHotelIdAndCheckOut(hotelId, date);
        else return reservationRepo.findByHotelIdAndCheckIn(hotelId, date);
    }

    @Override
    public List<Reservation> findFilteredReservationsByRoomType(Long roomTypeId) {
        return reservationRepo.findByRoomTypeId(roomTypeId);
    }

    @Override
    public Page<Reservation> getReservationsByHotelName(String hotelName,
                                                        Pageable pageable) {
        return reservationRepo.findByHotelName(hotelName, pageable);
    }

    @Override
    public List<Reservation> findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(Room room,
                                                                                          LocalDate checkOutDate,
                                                                                          LocalDate checkInDate) {
        return reservationRepo.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(room, checkInDate, checkOutDate);
    }

    @Override
    public void deleteReservation(Long id) {
        reservationRepo.deleteById(id);
    }

    @Override
    public void deleteReservationWithConformation(Long id, Long hotelId) {
        try {
            Reservation reservation = reservationRepo.findById(id).orElseThrow(() -> new NotFoundException("Reservation not found"));
            if (!Objects.equals(reservation.getRoomType().getId(), hotelId))
                throw new IllegalAccessException("Manager hotel doesn't match with reservation hotel");
            reservationRepo.delete(reservation);
        } catch (NotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Reservation> findByRoom(Room room) {
        return reservationRepo.findByRoom(room);
    }

    @Override
    public List<Reservation> findByRoomType(RoomType filter) {
        return reservationRepo.findByRoomRoomType(filter);
    }

}


