package com.example.smsbackend.services;

import com.example.smsbackend.entities.Announcement;
import com.example.smsbackend.entities.Subject;
import com.example.smsbackend.repositories.AnnouncementRepository;
import com.example.smsbackend.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // Method to create an announcement
    public Announcement createAnnouncement(Long subjectId, String title, String description, LocalDateTime scheduledFor) {
        // Find the subject by ID
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));

        // Create and save the announcement
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setDescription(description);
        announcement.setSubject(subject);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setScheduledFor(scheduledFor);

        return announcementRepository.save(announcement);
    }

    // Method to get announcements by subject
    public List<Announcement> getAnnouncementsBySubject(Long subjectId) {
        // Find the subject by ID
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));

        // Return all announcements for the subject
        return announcementRepository.findBySubject(subject);
    }
    // Method to delete an announcement by ID
    public void deleteAnnouncement(Long announcementId) {
        // Check if the announcement exists in the database
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found with ID: " + announcementId));

        // Delete the announcement
        announcementRepository.delete(announcement);
    }
}
