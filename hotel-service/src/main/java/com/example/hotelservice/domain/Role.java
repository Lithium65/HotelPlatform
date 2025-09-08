package com.example.hotelservice.domain;

import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

@Table(name = "user_role")
public enum Role implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
