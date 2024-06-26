package com.visma_project.meetingmanager.service;

import com.visma_project.meetingmanager.model.*;
import com.visma_project.meetingmanager.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository repository;

    public List<Meeting> getAllMeetings() {
        return repository.findAll();
    }

    public void createMeeting(Meeting meeting) {
        repository.save(meeting);
    }

    public void deleteMeeting(String name, String responsiblePerson) {
        Optional<Meeting> meetingOpt = repository.findById(name);
        if (meetingOpt.isPresent()) {
            Meeting meeting = meetingOpt.get();
            if (meeting.getResponsiblePerson().equals(responsiblePerson)) {
                repository.delete(meeting);
            } else {
                throw new IllegalArgumentException("Only the responsible person can delete the meeting.");
            }
        } else {
            throw new IllegalArgumentException("Meeting not found.");
        }
    }

    public void addPersonToMeeting(String meetingName, String person, LocalDateTime startTime) {
        Optional<Meeting> meetingOpt = repository.findById(meetingName);
        if (meetingOpt.isPresent()) {
            Meeting meeting = meetingOpt.get();
            if (meeting.getAttendees().contains(person)) {
                throw new IllegalArgumentException("Person is already in the meeting.");
            }
            System.out.println("CHECK");
            for (Meeting m : repository.findAll()) {
                if (m.getAttendees().contains(person)) {
                    if (startTime.isEqual(m.getStartDate()) || startTime.isBefore(m.getEndDate()) && startTime.isAfter(m.getStartDate())) {
                        throw new IllegalArgumentException("Person is already in another meeting at this time.");
                    }
                }
            }

            meeting.getAttendees().add(person);
            repository.saveToFile();
        } else {
            throw new IllegalArgumentException("Meeting not found.");
        }
    }

    public void removePersonFromMeeting(String meetingName, String person) {
    Optional<Meeting> meetingOpt = repository.findById(meetingName);
    if (meetingOpt.isPresent()) {
        Meeting meeting = meetingOpt.get();
        if (meeting.getResponsiblePerson().equals(person)) {
            throw new IllegalArgumentException("The responsible person cannot be removed from the meeting.");
        }

        if (!meeting.getAttendees().contains(person)) {
            throw new IllegalArgumentException("Person is not in the meeting.");
        }

        meeting.getAttendees().remove(person);
        repository.saveToFile();
        } else {
        throw new IllegalArgumentException("Meeting not found.");
        }
    }

    public List<Meeting> filterMeetings(String description, String responsiblePerson, Category category, Type type, LocalDateTime startDate, LocalDateTime endDate, Integer minAttendees) {
        return repository.findAll().stream()
                .filter(meeting -> description == null || meeting.getDescription().toLowerCase().contains(description.toLowerCase()))
                .filter(meeting -> responsiblePerson == null || meeting.getResponsiblePerson().equals(responsiblePerson))
                .filter(meeting -> category == null || meeting.getCategory() == category)
                .filter(meeting -> type == null || meeting.getType() == type)
                .filter(meeting -> startDate == null || meeting.getStartDate().isAfter(startDate))
                .filter(meeting -> endDate == null || meeting.getStartDate().isBefore(endDate))
                .filter(meeting -> minAttendees == null || meeting.getAttendees().size() >= minAttendees)
                .collect(Collectors.toList());
    }
}
