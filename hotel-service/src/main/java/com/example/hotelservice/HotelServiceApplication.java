package com.example.hotelservice;

import com.example.hotelservice.domain.Room;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;

@SpringBootApplication
public class HotelServiceApplication
{
	public static void main(String[] args) {
		SpringApplication.run(HotelServiceApplication.class, args);
	}

	public interface RoomRepo extends CrudRepository<Room, Long> {

	}
}