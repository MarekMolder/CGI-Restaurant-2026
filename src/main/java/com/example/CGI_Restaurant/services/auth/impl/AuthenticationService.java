package com.example.CGI_Restaurant.services.auth.impl;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    UserDetails authenticate(String email, String password);
    void register(String email, String password, String name);
    String generateToken(UserDetails userDetails);
    UserDetails validateToken(String token);
}
