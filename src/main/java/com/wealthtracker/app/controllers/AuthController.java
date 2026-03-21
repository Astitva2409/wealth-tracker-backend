package com.wealthtracker.app.controllers;

import com.wealthtracker.app.dto.AuthResponseDto;
import com.wealthtracker.app.dto.LoginRequestDto;
import com.wealthtracker.app.dto.SignupRequestDto;
import com.wealthtracker.app.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Signup and Login endpoints")
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/signup
    // Public route — no JWT needed
    // @Valid triggers the validation annotations on SignupRequestDto
    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponseDto> signup(
            @RequestBody @Valid SignupRequestDto signupRequestDto) {
        AuthResponseDto response = authService.signup(signupRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // POST /api/auth/login
    // Public route — no JWT needed
    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<AuthResponseDto> login(
            @RequestBody @Valid LoginRequestDto loginRequestDto) {
        AuthResponseDto response = authService.login(loginRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}