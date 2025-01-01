package com.example.smsbackend.repositories;


import com.example.smsbackend.entities.EnrolledSubject;
import com.example.smsbackend.entities.Subject;
import com.example.smsbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrolledSubjectRepository extends JpaRepository<EnrolledSubject, Long> {
    // Custom query to check if a student is already enrolled in a subject
    boolean existsBySubjectAndStudent(Subject subject, User student);

    // Method to find all students enrolled in a subject
    List<EnrolledSubject> findBySubject(Subject subject);

    // Custom query to find enrolled subjects by student
    List<EnrolledSubject> findByStudent(User student);
}


