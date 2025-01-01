package com.example.smsbackend.controllers;

import com.example.smsbackend.dtos.UpdateUserDto;
import com.example.smsbackend.entities.User;
import com.example.smsbackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_PRINCIPAL') or hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
        public ResponseEntity<User> updateUser(@PathVariable Integer userId, @RequestBody UpdateUserDto updateUserDto) {
        User updatedUser = userService.updateUser(userId, updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }
}
