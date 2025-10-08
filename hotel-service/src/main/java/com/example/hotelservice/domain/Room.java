package com.example.hotelservice.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long roomId;

    public Long getRoomId() {
        return roomId;
    }

    public int number;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    public RoomType roomType;

    public Room() {
    }

    public String getRoomTypeName() {
        return roomType != null ? roomType.getName() : "<Неизвестный тип комнаты>";
    }

    public Room(int number, RoomType roomType) {
        this.number = number;
        this.roomType = roomType;

    }


    public int getNumber() {
        return number;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
