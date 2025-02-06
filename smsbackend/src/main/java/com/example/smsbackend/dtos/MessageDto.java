package com.example.smsbackend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private Integer senderId;
    private Integer receiverId;
    private String content;
}
