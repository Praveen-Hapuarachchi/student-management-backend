package com.example.smsbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Getter
@Setter
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Title of the announcement

    @Column(nullable = false, length = 1000)
    private String description; // Detailed description

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject; // Associated subject

    @Column(nullable = false)
    private LocalDateTime createdAt; // When the announcement was created

    @Column(nullable = false)
    private LocalDateTime scheduledFor; // When the event (quiz, exam) is scheduled
}
