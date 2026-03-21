package com.wealthtracker.app.security;

import com.wealthtracker.app.exception.ResourceNotFoundException;
import com.wealthtracker.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository userRepository;

    // Spring Security calls this during login to load the user by email
    // It then compares the stored BCrypt hash with the provided password
    // We use email as the "username" — same as getUsername() in User entity
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> (UserDetails) userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email));
    }
}