package com.example.hotelservice.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    private String phNumber;

    private String firstName;

    private String lastName;

    private String country;

    public String getRoomTypeName(){
        return room.getRoom_type() != null ? room.getRoom_type().getName() : "<Неизвестный тип комнаты>";
    }

    public RoomType getRoomType(){
        return room.getRoom_type();
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public User getUser() {
        return user;
    }

    public Room getRoom() {
        return room;
    }

    public Long getRoomId() { return room.getRoomId(); }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Reservation() {
    }

    public Reservation(String phNumber, String firstName, String lastName, String country, User user, LocalDate checkIn, LocalDate checkOut){
        this.phNumber = phNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.user = user;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

//    public RoomType getRoomType() { return room.getRoom_type(); }
}
