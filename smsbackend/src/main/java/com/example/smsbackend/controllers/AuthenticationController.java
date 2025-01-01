package com.example.smsbackend.controllers;

import com.example.smsbackend.dtos.LoginUserDto;
import com.example.smsbackend.dtos.RegisterUserDto;
import com.example.smsbackend.entities.User;
import com.example.smsbackend.responses.LoginResponse;
import com.example.smsbackend.services.AuthenticationService;
import com.example.smsbackend.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        // Generate JWT Token with role
        String jwtToken = jwtService.generateToken(authenticatedUser, authenticatedUser.getRole(), authenticatedUser.getFullName(), authenticatedUser.getId());
        long expirationTime = jwtService.getExpirationTime();

        // Prepare response with token and role
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(expirationTime);
        loginResponse.setRole(authenticatedUser.getRole());
        loginResponse.setFullName(authenticatedUser.getFullName());
        loginResponse.setId(authenticatedUser.getId());

        return ResponseEntity.ok(loginResponse);
    }
}
