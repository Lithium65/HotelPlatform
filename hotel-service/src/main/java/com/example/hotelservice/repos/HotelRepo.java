package com.example.hotelservice.repos;

import com.example.hotelservice.domain.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepo extends JpaRepository<Hotel, Long> {
    @Query("SELECT DISTINCT h.city FROM Hotel h WHERE LOWER(h.city) LIKE LOWER(CONCAT(:query, '%'))")
    List<String> findDistinctCitiesByCityStartingWithIgnoreCase(@Param("query") String query);

    @Query("SELECT h FROM Hotel h WHERE h.id = :hotelId")
    Hotel getHotelById(@Param("hotelId") Long hotelId);

    List<Hotel> findByCountry(String country);

    Page<Hotel> findByCountry(String country, Pageable pageable);

    List<Hotel> findByCity(String city);

    List<Hotel> findByCountryAndCity(String country, String city);

    Hotel findByHotelName(String hotelName);
}
