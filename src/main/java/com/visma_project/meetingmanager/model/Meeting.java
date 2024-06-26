package com.visma_project.meetingmanager.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class Meeting {

    private String name;
    private String responsiblePerson;
    private String description;
    private Category category;
    private Type type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Set<String> attendees = new HashSet<>();



}
