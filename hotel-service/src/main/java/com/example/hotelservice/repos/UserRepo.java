package com.example.hotelservice.repos;

import com.example.hotelservice.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Page<User> findByUsernameContaining(String username, Pageable pageable);
}
