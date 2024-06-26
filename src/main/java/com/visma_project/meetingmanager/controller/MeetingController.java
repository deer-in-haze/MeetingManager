package com.visma_project.meetingmanager.controller;

import com.visma_project.meetingmanager.model.Category;
import com.visma_project.meetingmanager.model.Meeting;
import com.visma_project.meetingmanager.model.Type;
import com.visma_project.meetingmanager.service.MeetingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @GetMapping("/all")
    public List<Meeting> getAllMeetings(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String responsiblePerson,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer minAttendees) {
        return meetingService.filterMeetings(description, responsiblePerson, category, type, startDate, endDate, minAttendees);
    }

    @PostMapping
    public ResponseEntity<String> createMeeting(@RequestBody Meeting meeting) {
        meetingService.createMeeting(meeting);
        return ResponseEntity.ok("Meeting created successfully.");
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteMeeting(@PathVariable String name, @RequestParam String responsiblePerson) {
        try {
            meetingService.deleteMeeting(name, responsiblePerson);
            return ResponseEntity.ok("Meeting deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{meetingName}/addPerson")
    public ResponseEntity<String> addPersonToMeeting(
            @PathVariable String meetingName,
            @RequestParam String person,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        try {
            meetingService.addPersonToMeeting(meetingName, person, startTime);
            return ResponseEntity.ok("Person added to meeting successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{meetingName}/removePerson")
    public ResponseEntity<String> removePersonFromMeeting(
            @PathVariable String meetingName,
            @RequestParam String person) {
        try {
            meetingService.removePersonFromMeeting(meetingName, person);
            return ResponseEntity.ok("Person removed from meeting successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
