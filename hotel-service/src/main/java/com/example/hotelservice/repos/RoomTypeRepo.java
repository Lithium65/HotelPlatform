package com.example.hotelservice.repos;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.domain.Reservation;
import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomTypeRepo extends JpaRepository<RoomType, Long> {
    List<RoomType> findByHotelId(Long hotel_id);

    Page<RoomType> findByHotelId(Long hotelId, Pageable pageable);

}

