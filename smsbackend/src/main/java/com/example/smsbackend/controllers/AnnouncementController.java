package com.example.smsbackend.controllers;

import com.example.smsbackend.dtos.CreateAnnouncementRequest;
import com.example.smsbackend.entities.Announcement;
import com.example.smsbackend.services.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping("/create")
    public ResponseEntity<Announcement> createAnnouncement(
            @RequestBody CreateAnnouncementRequest request) {

        // Parse the String to LocalDateTime (assuming request.getScheduledFor() is in ISO-8601 format)
        LocalDateTime scheduledForDateTime = LocalDateTime.parse(request.getScheduledFor());

        // Create the announcement with the parsed LocalDateTime
        Announcement announcement = announcementService.createAnnouncement(
                request.getSubjectId(),
                request.getTitle(),
                request.getDescription(),
                scheduledForDateTime
        );

        // Return the created announcement
        return ResponseEntity.ok(announcement);
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Announcement>> getAnnouncementsBySubject(@PathVariable Long subjectId) {
        List<Announcement> announcements = announcementService.getAnnouncementsBySubject(subjectId);
        return ResponseEntity.ok(announcements);
    }

    // Add the delete announcement method
    @DeleteMapping("/delete/{announcementId}")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable Long announcementId) {
        try {
            // Call the service method to delete the announcement
            announcementService.deleteAnnouncement(announcementId);
            return ResponseEntity.ok("Announcement deleted successfully");
        } catch (RuntimeException e) {
            // If the announcement is not found, return an error message
            return ResponseEntity.status(404).body("Announcement not found");
        }
    }
}
