package com.example.smsbackend.services;

import com.example.smsbackend.dtos.LoginUserDto;
import com.example.smsbackend.dtos.RegisterUserDto;
import com.example.smsbackend.entities.User;
import com.example.smsbackend.repositories.UserRepository;
import com.example.smsbackend.responses.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        User user = new User(); // Create an instance of User
        user.setFullName(input.getFullName()); // Set fullName
        user.setEmail(input.getEmail()); // Set email
        user.setPassword(passwordEncoder.encode(input.getPassword())); // Set password

        // Set default role or assign from DTO (here setting default)
        user.setRole("ROLE_" + input.getRole().toUpperCase()); // or input.getRole() if passing it from the DTO
        return userRepository.save(user); // Save the user
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}
