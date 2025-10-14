package com.example.hotelservice.services;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HotelService {
    Optional<Hotel> getHotelById(Long id);

    void createHotel(Hotel hotel);

    void updateHotel(Hotel hotel);

    List<Hotel> findAll();

    Page<Hotel> getAllHotels(Pageable pageable);

    void deleteHotel(Long id);

    Page<Hotel> getHotelsByCountry(String country, Pageable pageable);

    void assignHotelToManager(User user, Long hotelId);

    void releaseHotelFromManager(User user, Long hotelId);
}
