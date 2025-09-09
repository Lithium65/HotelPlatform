package com.example.hotelservice.services.impl;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.repos.HotelRepo;
import com.example.hotelservice.services.HotelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {
    private final HotelRepo hotelRepo;

    public HotelServiceImpl(HotelRepo hotelRepo) {
        this.hotelRepo = hotelRepo;
    }

    @Override
    public Hotel getHotelById(Long id) { return hotelRepo.getHotelById(id); }

    @Override
    public void createHotel(Hotel hotel) { hotelRepo.save(hotel); }

    @Override
    public void updateHotel(Hotel hotel) {
        Hotel existingHotel = hotelRepo.findById(hotel.getId()).orElse(null);
        existingHotel = hotel;
        hotelRepo.save(existingHotel);
    }

    @Override
    public List<Hotel> findAll(){
        return hotelRepo.findAll();
    }

    @Override
    public Page<Hotel> getAllHotels(Pageable pageable) {
        return hotelRepo.findAll(pageable);
    }

    @Override
    public void deleteHotel(Long id) { hotelRepo.deleteById(id); }

    @Override
    public Page<Hotel> getHotelsByCountry(String country, Pageable pageable) {
        return hotelRepo.findByCountry(country, pageable);
    }

}
