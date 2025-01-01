package com.example.smsbackend.dtos;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAnnouncementRequest {
    private Long subjectId;
    private String title;
    private String description;
    private String scheduledFor; // ISO-8601 format date-time string
}
