package com.visma_project.meetingmanager.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visma_project.meetingmanager.model.Meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MeetingRepository {

    private final String filePath = "meetings.json";
    private List<Meeting> meetings;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    public void init() {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                System.out.println("Reading meetings from " + filePath);
                meetings = mapper.readValue(file, new TypeReference<List<Meeting>>() {});
            } else {
                System.out.println("File not found. Initializing new meetings list.");
                meetings = new ArrayList<>();
                mapper.writeValue(file, meetings);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize repository", e);
        }
    }

    public List<Meeting> findAll() {
        return meetings;
    }

    public void save(Meeting meeting) {
        meetings.add(meeting);
        saveToFile();
    }

    public void delete(Meeting meeting) {
        meetings.remove(meeting);
        saveToFile();
    }

    public Optional<Meeting> findById(String name) {
        return meetings.stream().filter(m -> m.getName().equals(name)).findFirst();
    }

    public void saveToFile() {
        try {
            mapper.writeValue(new File(filePath), meetings);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not save meetings to file", e);
        }
    }
}
