package com.example.smsbackend.controllers;

import com.example.smsbackend.entities.Subject;
import com.example.smsbackend.entities.SubjectMaterial;
import com.example.smsbackend.entities.User;
import com.example.smsbackend.repositories.SubjectRepository;
import com.example.smsbackend.repositories.UserRepository;
import com.example.smsbackend.repositories.EnrolledSubjectRepository;
import com.example.smsbackend.services.SubjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;



import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrolledSubjectRepository enrolledSubjectRepository;


    @PostMapping("/create")
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectService.createSubject(subject);
    }

    // New endpoint to get subjects by teacher
    @GetMapping("/my-subjects")
    public List<Subject> getSubjectsByTeacher() {
        return subjectService.getSubjectsByTeacher();
    }

    @GetMapping("/{id}")
    public Subject getSubjectById(@PathVariable Long id) {
        return subjectService.getSubjectById(id);
    }

    @GetMapping("/all")
    public List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @PostMapping("/{subjectId}/enroll")
    public ResponseEntity<String> enrollStudent(@PathVariable Long subjectId, @RequestParam Long studentId) {
        try {
            subjectService.enrollStudent(subjectId, studentId);  // Pass Long directly
            return ResponseEntity.ok("Student enrolled successfully");
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{subjectId}/enrollment-status")
    public ResponseEntity<Map<String, Boolean>> checkEnrollmentStatus(@PathVariable Long subjectId, @RequestParam Long studentId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        User student = userRepository.findById(Math.toIntExact(studentId))
                .orElseThrow(() -> new RuntimeException("Student not found"));

        boolean isEnrolled = enrolledSubjectRepository.existsBySubjectAndStudent(subject, student);
        return ResponseEntity.ok(Collections.singletonMap("isEnrolled", isEnrolled));
    }

    @GetMapping("/{subjectId}/students")
    public ResponseEntity<List<User>> getEnrolledStudents(@PathVariable Long subjectId) {
        try {
            log.info("Fetching enrolled students for subject ID: {}", subjectId);
            List<User> students = subjectService.getEnrolledStudents(subjectId);
            log.info("Fetched students: {}", students);
            return ResponseEntity.ok(students);
        } catch (RuntimeException e) {
            log.error("Error fetching students: ", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/enrolled")
    public ResponseEntity<List<Subject>> getEnrolledSubjectsForStudent() {
        try {
            List<Subject> subjects = subjectService.getEnrolledSubjectsForStudent();
            return ResponseEntity.ok(subjects);
        } catch (RuntimeException e) {
            log.error("Error fetching enrolled subjects: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    // Upload material
    @PostMapping("/{subjectId}/materials/upload")
    public ResponseEntity<String> uploadMaterial(
            @PathVariable Long subjectId,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty. Please upload a valid file.");
            }

            // You can add more validation for file types here, if needed.
            subjectService.uploadMaterial(subjectId, file.getOriginalFilename(), file.getContentType(), file.getBytes());
            return ResponseEntity.ok("File uploaded successfully");
        } catch (RuntimeException e) {
            // Handle cases like Subject not found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }


    // Download materials
    @GetMapping("/{subjectId}/materials")
    public ResponseEntity<List<SubjectMaterial>> getMaterials(@PathVariable Long subjectId) {
        List<SubjectMaterial> materials = subjectService.getMaterialsBySubjectId(subjectId);
        return ResponseEntity.ok(materials);
    }

    @DeleteMapping("/{subjectId}/materials/{materialId}")
    public ResponseEntity<String> deleteMaterial(
            @PathVariable Long subjectId,
            @PathVariable Long materialId) {
        try {
            subjectService.deleteMaterialById(materialId);
            return ResponseEntity.ok("Material deleted successfully");
        } catch (RuntimeException e) {
            // Handle cases like Material or Subject not found
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete material: " + e.getMessage());
        }
    }




    // Download specific material by ID
    @CrossOrigin(origins = "http://localhost:3000") // Allow frontend origin
    @GetMapping("/{subjectId}/materials/{materialId}/download")
    public ResponseEntity<byte[]> downloadMaterial(
            @PathVariable Long subjectId,
            @PathVariable Long materialId) {
        try {
            // Fetch the material by ID
            SubjectMaterial material = subjectService.getMaterialById(materialId);

            // Set response headers for downloading
            return ResponseEntity.ok()
                    .header("Content-Type", material.getFileType())
                    .header("Content-Disposition", "attachment; filename=\"" + material.getFileName() + "\"")
                    .body(material.getData());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);  // Material not found
        }
    }




}
