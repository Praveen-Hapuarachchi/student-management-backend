package com.example.smsbackend.services;

import com.example.smsbackend.entities.EnrolledSubject;
import com.example.smsbackend.entities.Subject;
import com.example.smsbackend.entities.SubjectMaterial;
import com.example.smsbackend.entities.User;
import com.example.smsbackend.repositories.EnrolledSubjectRepository;
import com.example.smsbackend.repositories.SubjectMaterialRepository;
import com.example.smsbackend.repositories.SubjectRepository;
import com.example.smsbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;


import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EnrolledSubjectRepository enrolledSubjectRepository;

    @Autowired
    private SubjectMaterialRepository materialRepository;


    public Subject createSubject(Subject subject) {
        // Get the logged-in user's email from SecurityContextHolder
        String teacherEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Ensure that only teachers can create subjects
        if (!"ROLE_TEACHER".equals(teacher.getRole())) {
            throw new RuntimeException("Only teachers can create subjects");
        }

        subject.setTeacher(teacher);
        return subjectRepository.save(subject);
    }

    // New method to get subjects by teacher
    public List<Subject> getSubjectsByTeacher() {
        String teacherEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return subjectRepository.findByTeacher(teacher);
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
    @Transactional
    public void enrollStudent(Long subjectId, Long studentId) {
        // Find the subject and ensure it's valid
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found for ID: " + subjectId));

        // Find the student and ensure they're valid
        User student = userRepository.findById(Math.toIntExact(studentId))
                .orElseThrow(() -> new EntityNotFoundException("Student not found for ID: " + studentId));

        if (!"ROLE_STUDENT".equals(student.getRole())) {
            throw new IllegalArgumentException("User with ID " + studentId + " is not a student");
        }

        // Check if the student is already enrolled in the subject
        boolean alreadyEnrolled = enrolledSubjectRepository.existsBySubjectAndStudent(subject, student);
        if (alreadyEnrolled) {
            throw new IllegalArgumentException("Student with ID " + studentId + " is already enrolled in subject " + subjectId);
        }

        // Enroll the student by saving the EnrolledSubject entity
        EnrolledSubject enrolledSubject = new EnrolledSubject(subject, student);
        enrolledSubjectRepository.save(enrolledSubject);
    }

    public List<User> getEnrolledStudents(Long subjectId) {
        log.info("Looking for subject with ID: {}", subjectId);

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> {
                    log.error("Subject not found for ID: {}", subjectId);
                    return new RuntimeException("Subject not found");
                });

        log.info("Subject found: {}", subject);

        List<EnrolledSubject> enrolledSubjects = enrolledSubjectRepository.findBySubject(subject);

        log.info("Enrolled subjects: {}", enrolledSubjects);

        return enrolledSubjects.stream()
                .map(EnrolledSubject::getStudent)
                .collect(Collectors.toList());
    }

    public List<Subject> getEnrolledSubjectsForStudent() {
        // Get the logged-in student's email from the SecurityContext
        String studentEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // Find the student by email
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Ensure the logged-in user has the role of a student
        if (!"ROLE_STUDENT".equals(student.getRole())) {
            throw new RuntimeException("Only students can view enrolled subjects");
        }

        // Fetch the enrolled subjects for the student
        List<EnrolledSubject> enrolledSubjects = enrolledSubjectRepository.findByStudent(student);

        // Map the enrolled subjects to their associated subjects
        return enrolledSubjects.stream()
                .map(EnrolledSubject::getSubject)
                .collect(Collectors.toList());
    }


    public void uploadMaterial(Long subjectId, String fileName, String fileType, byte[] data) {
        // Check if the subject exists
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));

        // Create and save the subject material
        SubjectMaterial material = new SubjectMaterial();
        material.setFileName(fileName);
        material.setFileType(fileType);
        material.setData(data);
        material.setSubject(subject);

        materialRepository.save(material);
    }

    public List<SubjectMaterial> getMaterialsBySubjectId(Long subjectId) {
        return materialRepository.findBySubjectId(subjectId);
    }

    @Transactional
    public void deleteMaterialById(Long materialId) {
        // Check if the material exists
        SubjectMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found with ID: " + materialId));

        // Delete the material
        materialRepository.delete(material);
    }

    public SubjectMaterial getMaterialById(Long materialId) {
        return materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found for ID: " + materialId));
    }


}

