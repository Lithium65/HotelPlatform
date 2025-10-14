package com.example.hotelservice.services;

import com.example.hotelservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> getAllUsers();

    Page<User> getAllUsers(Pageable pageable);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    void save(User user);

    Page<User> findByUsernameContaining(String username, Pageable pageable);

    User findByUsername(String username);

    void updateUser(Long id, String username, List<String> roles, Long hotelId);
}
