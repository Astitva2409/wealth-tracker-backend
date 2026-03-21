package com.wealthtracker.app.services.impl;

import com.wealthtracker.app.entities.User;
import com.wealthtracker.app.exception.ResourceNotFoundException;
import com.wealthtracker.app.repository.UserRepository;
import com.wealthtracker.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        // Same orElseThrow pattern as your Uber project
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));
    }
}