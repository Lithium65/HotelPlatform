package com.example.hotelservice.services;

import com.example.hotelservice.domain.Reservation;
import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<Reservation> getAllReservations();

    Page<Reservation> getAllReservations(Pageable pageable);

    Iterable<Reservation> getReservationsByUser(User user);

    Optional<Reservation> getReservationById(long id);

    Page<Reservation> findByHotelId(@Param("hotelId") Long hotelId, Pageable pageable);

    List<Reservation> findByHotelId(@Param("hotelId") Long hotelId);

    void createReservation(Reservation reservation, RoomType roomType);

    List<Reservation> findFilteredReservationsByRoomTypeAndDate(Long roomTypeId, LocalDate date, Boolean isCheckOut);

    List<Reservation> findFilteredReservationsByDate(Long hotelId, LocalDate date, Boolean isCheckOut);

    List<Reservation> findFilteredReservationsByRoomType(Long roomTypeId);

    Page<Reservation> getReservationsByHotelName(String hotelName, Pageable pageable);

    void deleteReservation(Long id);

    void deleteReservationWithConformation(Long id, Long hotelId);

    List<Reservation> findByRoom(Room room);

    List<Reservation> findByRoomType(RoomType filter);

    List<Reservation> findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(Room room, LocalDate checkOutDate, LocalDate checkInDate);
}
