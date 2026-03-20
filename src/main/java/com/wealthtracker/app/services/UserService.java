package com.wealthtracker.app.services;

import com.wealthtracker.app.entities.User;

public interface UserService {

    // Called by JwtAuthFilter on every authenticated request
    User getUserById(Long id);

    // Called by AuthServiceImpl during login
    User getUserByEmail(String email);
}