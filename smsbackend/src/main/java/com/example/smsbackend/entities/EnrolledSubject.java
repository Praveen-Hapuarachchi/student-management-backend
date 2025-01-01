package com.example.smsbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "enrolled_subjects")
@Getter
@Setter
@NoArgsConstructor
public class EnrolledSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Other fields like enrollment date can be added if needed

    public EnrolledSubject(Subject subject, User student) {
        this.subject = subject;
        this.student = student;
    }

    // Getters and setters
}
