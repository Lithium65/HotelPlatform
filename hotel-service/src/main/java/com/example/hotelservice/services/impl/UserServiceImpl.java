package com.example.hotelservice.services.impl;

import com.example.hotelservice.domain.User;
import com.example.hotelservice.repos.UserRepo;
import com.example.hotelservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public List<User> getAllUsers() { return userRepo.findAll(); }

    @Override
    public Page<User> getAllUsers(Pageable pageable) { return userRepo.findAll(pageable); }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    @Override
    public void save(User user) { userRepo.save(user); }

    @Override
    public Page<User> findByUsernameContaining(String username, Pageable pageable) {
        return userRepo.findByUsernameContaining(username, pageable);
    }

    @Override
    public User findByUsername(String username) { return userRepo.findByUsername(username);}

}

