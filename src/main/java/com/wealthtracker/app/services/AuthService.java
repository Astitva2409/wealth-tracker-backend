package com.wealthtracker.app.services;

import com.wealthtracker.app.dto.AuthResponseDto;
import com.wealthtracker.app.dto.LoginRequestDto;
import com.wealthtracker.app.dto.SignupRequestDto;

public interface AuthService {

    AuthResponseDto signup(SignupRequestDto signupRequestDto);

    AuthResponseDto login(LoginRequestDto loginRequestDto);
}