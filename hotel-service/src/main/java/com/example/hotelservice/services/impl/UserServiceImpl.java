package com.example.hotelservice.services.impl;

import com.example.hotelservice.domain.Role;
import com.example.hotelservice.domain.User;
import com.example.hotelservice.repos.UserRepo;
import com.example.hotelservice.services.HotelService;
import com.example.hotelservice.services.UserService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private HotelService hotelService;

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    @Override
    public void save(User user) {
        userRepo.save(user);
    }

    @Override
    public Page<User> findByUsernameContaining(String username, Pageable pageable) {
        return userRepo.findByUsernameContaining(username, pageable);
    }

    @Override
    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public void updateUser(Long id, String username, List<String> updatedRoles, Long hotelId) {
        try {
            User user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
            user.setUsername(username);
            Set<Role> originRoles = new HashSet<>();
            for (String role : updatedRoles) {
                originRoles.add(Role.valueOf(role));
            }
            if (hotelId != null && originRoles.contains(Role.MANAGER)) {
                hotelService.assignHotelToManager(user, hotelId);
            } else if (user.getRoles().contains(Role.MANAGER) && !originRoles.contains(Role.MANAGER)) {
                hotelService.releaseHotelFromManager(user, hotelId);
            }
            user.setRoles(originRoles);
            userRepo.save(user);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

