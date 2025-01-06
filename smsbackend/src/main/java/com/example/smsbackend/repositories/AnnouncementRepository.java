package com.example.smsbackend.repositories;

import com.example.smsbackend.entities.Announcement;
import com.example.smsbackend.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findBySubject(Subject subject);
    void deleteBySubject(Subject subject); // Add a method to delete announcements by subject
}
