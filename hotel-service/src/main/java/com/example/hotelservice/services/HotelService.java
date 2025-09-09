package com.example.hotelservice.services;

import com.example.hotelservice.domain.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HotelService {
    Hotel getHotelById(Long id);

    void createHotel(Hotel hotel);

    void updateHotel(Hotel hotel);

    List<Hotel> findAll();

    Page<Hotel> getAllHotels(Pageable pageable);

    void deleteHotel(Long id);

    Page<Hotel> getHotelsByCountry(String country, Pageable pageable);
}
