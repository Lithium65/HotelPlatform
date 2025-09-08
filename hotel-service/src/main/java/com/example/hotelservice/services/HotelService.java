package com.example.hotelservice.services;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.repos.HotelRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HotelService {
    private final HotelRepo hotelRepo;

    public HotelService(HotelRepo hotelRepo) {
        this.hotelRepo = hotelRepo;
    }

    public List<Hotel> getAllHotels() {
        return hotelRepo.findAll();
    }

    public Hotel getHotelById(Long id) { return hotelRepo.getHotelById(id); }

    public void createHotel(Hotel hotel) { hotelRepo.save(hotel); }

    public void updateHotel(Hotel hotel) {
        Hotel existingHotel = hotelRepo.findById(hotel.getId()).orElse(null);
        existingHotel = hotel;
        hotelRepo.save(existingHotel);
    }

    public Optional<Hotel> findById(Long id){
        return hotelRepo.findById(id);
    }

    public List<Hotel> findByCountry(String country){
        return hotelRepo.findByCountry(country);
    }

    public List<Hotel> findByCity(String city){
        return hotelRepo.findByCity(city);
    }

    public List<Hotel> findAll(){
        return hotelRepo.findAll();
    }

    public List<Hotel> findByCountryAndCity(String country, String city){
        return hotelRepo.findByCountryAndCity(country, city);
    }

    public Page<Hotel> getAllHotels(Pageable pageable) {
        return hotelRepo.findAll(pageable);
    }

    public void deleteHotel(Long id) { hotelRepo.deleteById(id); }

    public Page<Hotel> getHotelsByCountry(String country, Pageable pageable) {
        return hotelRepo.findByCountry(country, pageable);
    }

}
