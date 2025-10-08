package com.example.hotelservice.services.impl;

import com.example.hotelservice.domain.Hotel;
import com.example.hotelservice.domain.Role;
import com.example.hotelservice.domain.User;
import com.example.hotelservice.repos.HotelRepo;
import com.example.hotelservice.services.HotelService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class HotelServiceImpl implements HotelService {
    @Autowired
    private final HotelRepo hotelRepo;

    public HotelServiceImpl(HotelRepo hotelRepo) {
        this.hotelRepo = hotelRepo;
    }

    @Override
    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepo.getHotelById(id);
    }

    @Override
    public void createHotel(Hotel hotel) {
        hotelRepo.save(hotel);
    }

    @Override
    public void updateHotel(Hotel hotel) {
        hotelRepo.save(hotel);
    }

    @Override
    public List<Hotel> findAll() {
        return hotelRepo.findAll();
    }

    @Override
    public Page<Hotel> getAllHotels(Pageable pageable) {
        return hotelRepo.findAll(pageable);
    }

    @Override
    public void deleteHotel(Long id) {
        hotelRepo.deleteById(id);
    }

    @Override
    public Page<Hotel> getHotelsByCountry(String country, Pageable pageable) {
        return hotelRepo.findByCountry(country, pageable);
    }

    @Override
    public void assignHotelToManager(User user, Long hotelId) {
        if (user.getManagedHotel() != null) {
            releaseHotelFromManager(user, hotelId);
        }
        try {
            Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(() -> new NotFoundException("Hotel not found"));
            user.setManagedHotel(hotel);
            hotel.getManagers().add(user);
            user.getRoles().add(Role.MANAGER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseHotelFromManager(User user, Long hotelId) {
        try {
            Hotel hotel = hotelRepo.getHotelById(hotelId).orElseThrow(() -> new NotFoundException("Hotel not found"));
            hotel.getManagers().remove(user);
            user.setManagedHotel(null);
            hotelRepo.save(hotel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
