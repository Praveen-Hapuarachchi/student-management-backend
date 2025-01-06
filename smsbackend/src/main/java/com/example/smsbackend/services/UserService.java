package com.example.smsbackend.services;

import com.example.smsbackend.dtos.UpdateUserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.smsbackend.entities.SubjectMaterial;
import com.example.smsbackend.entities.Subject;
import com.example.smsbackend.entities.User;
import com.example.smsbackend.entities.Announcement;
import com.example.smsbackend.repositories.UserRepository;
import com.example.smsbackend.repositories.SubjectRepository;
import com.example.smsbackend.repositories.SubjectMaterialRepository;
import com.example.smsbackend.repositories.AnnouncementRepository;
import org.springframework.stereotype.Service;
import com.example.smsbackend.repositories.EnrolledSubjectRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional // Ensure the deleteUser method runs within a transaction
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnrolledSubjectRepository enrolledSubjectRepository;
    private final SubjectRepository subjectRepository; // Add SubjectRepository dependency
    private final SubjectMaterialRepository subjectMaterialRepository;
    private final AnnouncementRepository announcementRepository; // Add AnnouncementRepository dependency

    // Constructor with all required repositories
    // Constructor with all required repositories
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EnrolledSubjectRepository enrolledSubjectRepository,
                       SubjectRepository subjectRepository,
                       SubjectMaterialRepository subjectMaterialRepository,
                       AnnouncementRepository announcementRepository) {  // Add AnnouncementRepository to constructor
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.enrolledSubjectRepository = enrolledSubjectRepository;
        this.subjectRepository = subjectRepository;
        this.subjectMaterialRepository = subjectMaterialRepository;
        this.announcementRepository = announcementRepository;  // Initialize AnnouncementRepository
    }



    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }

    public User updateUser(Integer userId, UpdateUserDto updateUserDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (updateUserDto.getFullName() != null) {
            user.setFullName(updateUserDto.getFullName());
        }
        if (updateUserDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }
        if (updateUserDto.getRole() != null) {
            user.setRole("ROLE_" + updateUserDto.getRole().toUpperCase());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Integer userId) {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Handle deletion for teachers
        if (user.getRole().equalsIgnoreCase("ROLE_TEACHER")) {
            // Delete all subjects assigned to the teacher
            List<Subject> subjects = subjectRepository.findByTeacher(user);
            for (Subject subject : subjects) {
                // Delete all related enrolled subjects
                enrolledSubjectRepository.deleteBySubjectId(subject.getId());

                // Delete all related announcements
                List<Announcement> announcements = announcementRepository.findBySubject(subject);
                for (Announcement announcement : announcements) {
                    announcementRepository.delete(announcement);
                }

                // Delete all related subject materials
                List<SubjectMaterial> materials = subjectMaterialRepository.findBySubjectId(subject.getId());
                subjectMaterialRepository.deleteAll(materials);

                // Delete the subject itself
                subjectRepository.delete(subject);
            }
        }

        // Handle deletion for students
        if (user.getRole().equalsIgnoreCase("ROLE_STUDENT")) {
            // Delete all enrolled subjects for the student to avoid foreign key constraint violations
            enrolledSubjectRepository.deleteByStudentId(userId);
        }

        // Finally, delete the user
        userRepository.delete(user);
    }



}
