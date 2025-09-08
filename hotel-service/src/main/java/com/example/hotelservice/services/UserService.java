package com.example.hotelservice.services;

import com.example.hotelservice.domain.User;
import com.example.hotelservice.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    public List<User> getAllUsers() { return userRepo.findAll(); }

    public Page<User> getAllUsers(Pageable pageable) { return userRepo.findAll(pageable); }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public void save(User user) { userRepo.save(user); }

    public Page<User> findByUsernameContaining(String username, Pageable pageable) {
        return userRepo.findByUsernameContaining(username, pageable);
    }

    public User findByUsername(String username) { return userRepo.findByUsername(username);}

}

