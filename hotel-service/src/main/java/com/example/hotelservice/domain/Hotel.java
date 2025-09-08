package com.example.hotelservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String googleMap;

    private String hotelName;

    private String street;

    private String city;

    private String country;

    private String description;

    @NotNull(message = "Файл не может быть пустым")
    private String filename1;

    public Hotel() {
    }

    public Hotel(Long id, String googleMap, String hotelName, String street, String city, String country, String description, String filename1) {
        this.id = id;
        this.googleMap = googleMap;
        this.hotelName = hotelName;
        this.street = street;
        this.city = city;
        this.country = country;
        this.description = description;
        this.filename1 = filename1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(String googleMap) {
        this.googleMap = googleMap;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilename1() {
        return filename1;
    }

    public void setFilename1(String filename1) {
        this.filename1 = filename1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
