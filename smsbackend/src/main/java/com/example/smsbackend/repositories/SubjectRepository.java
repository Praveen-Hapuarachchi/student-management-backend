package com.example.smsbackend.repositories;

import com.example.smsbackend.entities.Subject;
import com.example.smsbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Custom method to find subjects by teacher
    List<Subject> findByTeacher(User teacher);

}
