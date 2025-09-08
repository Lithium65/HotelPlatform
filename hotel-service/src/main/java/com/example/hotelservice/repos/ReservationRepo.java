package com.example.hotelservice.repos;

import com.example.hotelservice.domain.Reservation;
import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import com.example.hotelservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepo extends CrudRepository<Reservation, Long> {
    @Query("SELECT r.room.id FROM Reservation r WHERE r.checkIn <= :endDate AND r.checkOut >= :startDate")
    List<Long> findReservedRoomIdsByDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.room ro JOIN FETCH ro.roomType rt JOIN FETCH rt.hotel h")
    List<Reservation> findAllWithHotel();

    @Query("SELECT r FROM Reservation r JOIN FETCH r.room ro JOIN FETCH ro.roomType rt JOIN FETCH rt.hotel h WHERE h.hotelName LIKE %:hotelName%")
    Page<Reservation> findByHotelName(@Param("hotelName") String hotelName, Pageable pageable);

    List<Reservation> findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(Room room, LocalDate checkIn, LocalDate checkOut);

    Iterable<Reservation> findByUser(User user);

    List<Reservation> findByRoom(Optional<Room> room);

    List<Reservation> findByRoomRoomType(RoomType filter);

    Page<Reservation> findAll(Pageable pageable);

}
