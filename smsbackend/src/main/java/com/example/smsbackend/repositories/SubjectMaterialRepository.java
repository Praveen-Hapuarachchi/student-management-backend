package com.example.smsbackend.repositories;

import com.example.smsbackend.entities.SubjectMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectMaterialRepository extends JpaRepository<SubjectMaterial, Long> {
    List<SubjectMaterial> findBySubjectId(Long subjectId);
}
