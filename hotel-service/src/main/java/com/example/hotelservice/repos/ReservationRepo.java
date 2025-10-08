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
    @Query("SELECT r FROM Reservation r JOIN FETCH r.room ro JOIN FETCH ro.roomType rt JOIN FETCH rt.hotel h WHERE h.hotelName LIKE %:hotelName%")
    Page<Reservation> findByHotelName(@Param("hotelName") String hotelName, Pageable pageable);

    @Query("SELECT r FROM Reservation r " +
            "JOIN r.room room " +
            "JOIN room.roomType roomType " +
            "JOIN roomType.hotel hotel " +
            "WHERE hotel.id = :hotelId")
    Page<Reservation> findByHotelId(@Param("hotelId") Long hotelId, Pageable pageable);

    @Query("SELECT r FROM Reservation r " +
            "JOIN r.room room " +
            "JOIN room.roomType roomType " +
            "JOIN roomType.hotel hotel " +
            "WHERE hotel.id = :hotelId")
    List<Reservation> findByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT r FROM Reservation r " +
            "JOIN r.room room " +
            "JOIN room.roomType roomType " +
            "JOIN roomType.hotel hotel " +
            "WHERE hotel.id = :hotelId AND r.checkIn = :checkIn")
    List<Reservation> findByHotelIdAndCheckIn(@Param("hotelId") Long hotelId,
                                              @Param("checkIn") LocalDate checkIn);

    @Query("SELECT r FROM Reservation r " +
            "JOIN r.room room " +
            "JOIN room.roomType roomType " +
            "JOIN roomType.hotel hotel " +
            "WHERE hotel.id = :hotelId AND r.checkOut = :checkOut")
    List<Reservation> findByHotelIdAndCheckOut(@Param("hotelId") Long hotelId,
                                               @Param("checkOut") LocalDate checkOut);

    @Query("SELECT r FROM Reservation r WHERE room.roomType.id = :roomTypeId AND r.checkIn = :checkIn")
    List<Reservation> findByRoomAndCheckIn(@Param("roomType") Long roomTypeId, @Param("checkIn") LocalDate checkIn);

    @Query("SELECT r FROM Reservation r WHERE room.roomType.id = :roomTypeId AND r.checkOut = :checkOut")
    List<Reservation> findByRoomAndCheckOut(@Param("roomType") Long roomTypeId, @Param("checkOut") LocalDate checkOut);

    List<Reservation> findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(Room room, LocalDate checkIn, LocalDate checkOut);

    Iterable<Reservation> findByUser(User user);

    List<Reservation> findByRoom(Room room);

    List<Reservation> findByRoomRoomType(RoomType filter);

    @Query("SELECT r FROM Reservation r WHERE room.roomType.id = :roomTypeId")
    List<Reservation> findByRoomTypeId(@Param("roomTypeId") Long roomTypeId);

    Page<Reservation> findAll(Pageable pageable);

}
