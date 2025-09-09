package com.example.hotelservice.services;

import com.example.hotelservice.domain.Reservation;
import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    List<Reservation> getAllReservations();

    Page<Reservation> getAllReservations(Pageable pageable);

    Iterable<Reservation> getReservationsByUser(User user);

    Optional<Reservation> getReservationById(long id);

    void createReservation(Reservation reservation, RoomType roomType);

    Page<Reservation> getReservationsByHotelName(String hotelName, Pageable pageable);

    void deleteReservation(Long id);

    List<Reservation> findByRoom(Room room);

    List<Reservation> findByRoomType(RoomType filter);
}
