package com.example.smsbackend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {
    private String fullName;
    private String password;
    private String role;
}
