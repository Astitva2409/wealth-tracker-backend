package com.wealthtracker.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    // This is what the frontend receives after login/signup
    // Frontend stores the token and sends it in every request header
    private String token;
    private String name;
    private String email;
}