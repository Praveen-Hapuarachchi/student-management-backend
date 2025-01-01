package com.example.smsbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "subjects")
@Getter
@Setter
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    // Year field (e.g., 2024)
    @Column(nullable = false)
    private int year;

    // Grade field (e.g., "Grade 7", "Grade 8", etc.)
    @Column(nullable = false)
    private String grade;  // e.g., "Grade 7", "Grade 8"

    // Class field (e.g., "A", "B", "C")
    @Column(nullable = false)
    private String subjectClass;

}
