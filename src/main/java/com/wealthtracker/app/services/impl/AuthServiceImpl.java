package com.wealthtracker.app.services.impl;

import com.wealthtracker.app.dto.AuthResponseDto;
import com.wealthtracker.app.dto.LoginRequestDto;
import com.wealthtracker.app.dto.SignupRequestDto;
import com.wealthtracker.app.entities.User;
import com.wealthtracker.app.entities.enums.Role;
import com.wealthtracker.app.exception.RuntimeConflictException;
import com.wealthtracker.app.repository.UserRepository;
import com.wealthtracker.app.security.JwtService;
import com.wealthtracker.app.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDto signup(SignupRequestDto signupRequestDto) {

        // Check if email already registered
        // Same RuntimeConflictException pattern as Uber project
        boolean userExists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if (userExists) {
            throw new RuntimeConflictException("User already exists with email: "
                    + signupRequestDto.getEmail());
        }

        // Build the User entity
        User newUser = User.builder()
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getEmail())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .roles(Set.of(Role.USER))
                .build();

        User savedUser = userRepository.save(newUser);

        // Generate JWT token with the saved user's DB id
        String token = jwtService.generateToken(savedUser.getId());

        // Return token + user info to frontend
        // This is what replaces the fake token in AuthContext.tsx in Week 3
        return new AuthResponseDto(token, savedUser.getName(), savedUser.getEmail());
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {

        // AuthenticationManager handles credential verification
        // It calls UserDetailsService internally to load the user,
        // then compares the BCrypt hash — we don't do this manually
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        // If credentials are wrong, authenticate() throws
        // AuthenticationException — caught by GlobalExceptionHandler
        // If correct, we get the authenticated User principal back
        User authenticatedUser = (User) authentication.getPrincipal();

        // Generate fresh JWT token for this session
        String token = jwtService.generateToken(authenticatedUser.getId());

        // Week 3: React AuthContext.tsx replaces fakeLogin() with
        // axios.post('/api/auth/login') and reads this response
        return new AuthResponseDto(
                token,
                authenticatedUser.getName(),
                authenticatedUser.getEmail()
        );
    }
}