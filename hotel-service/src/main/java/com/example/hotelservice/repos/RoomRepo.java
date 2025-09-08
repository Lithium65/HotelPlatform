package com.example.hotelservice.repos;

import com.example.hotelservice.domain.Room;
import com.example.hotelservice.domain.RoomType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
    List<Room> findByRoomType(RoomType roomType);

    @Transactional
    void deleteById(Long id);

    @Transactional
    void deleteByRoomType(RoomType roomType);

    Optional<Room> findByNumberAndRoomType(int number, RoomType roomType);

}


