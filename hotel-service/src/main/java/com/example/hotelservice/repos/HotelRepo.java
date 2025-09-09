package com.example.hotelservice.repos;

import com.example.hotelservice.domain.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepo extends JpaRepository<Hotel, Long> {
    @Query("SELECT h FROM Hotel h WHERE h.id = :hotelId")
    Hotel getHotelById(@Param("hotelId") Long hotelId);

    Page<Hotel> findByCountry(String country, Pageable pageable);
}
