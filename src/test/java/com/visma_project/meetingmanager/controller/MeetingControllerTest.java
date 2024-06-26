package com.visma_project.meetingmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visma_project.meetingmanager.model.Category;
import com.visma_project.meetingmanager.model.Meeting;
import com.visma_project.meetingmanager.model.Type;
import com.visma_project.meetingmanager.service.MeetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.Collections;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeetingController.class)
class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetingService meetingService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllMeetings_shouldReturnEmptyList() throws Exception {
        when(meetingService.filterMeetings(anyString(), anyString(), any(Category.class), any(Type.class),
                any(LocalDateTime.class), any(LocalDateTime.class), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/meetings/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createMeeting_shouldReturnSuccessMessage() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setName("Test Meeting");

        mockMvc.perform(post("/api/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meeting)))
                .andExpect(status().isOk())
                .andExpect(content().string("Meeting created successfully."));
    }

    @Test
    void deleteMeeting_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/meetings/Test Meeting")
                .param("responsiblePerson", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(content().string("Meeting deleted successfully."));
    }

    @Test
    void deleteMeeting_shouldReturnErrorMessage() throws Exception {
        doThrow(new IllegalArgumentException("Meeting not found")).when(meetingService).deleteMeeting(anyString(),
                anyString());

        mockMvc.perform(delete("/api/meetings/Test Meeting")
                .param("responsiblePerson", "John Doe"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Meeting not found"));
    }

    @Test
    void addPersonToMeeting_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(post("/api/meetings/Test Meeting/addPerson")
                .param("person", "John Doe")
                .param("startTime", "2024-06-26T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().string("Person added to meeting successfully."));
    }

    @Test
    void addPersonToMeeting_shouldReturnErrorMessage() throws Exception {
        doThrow(new IllegalArgumentException("Person not found")).when(meetingService).addPersonToMeeting(anyString(),
                anyString(), any(LocalDateTime.class));

        mockMvc.perform(post("/api/meetings/Test Meeting/addPerson")
                .param("person", "John Doe")
                .param("startTime", "2024-06-26T10:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Person not found"));
    }

    @Test
    void removePersonFromMeeting_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/meetings/Test Meeting/removePerson")
                .param("person", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(content().string("Person removed from meeting successfully."));
    }

    @Test
    void removePersonFromMeeting_shouldReturnErrorMessage() throws Exception {
        doThrow(new IllegalArgumentException("Person not found")).when(meetingService)
                .removePersonFromMeeting(anyString(), anyString());

        mockMvc.perform(delete("/api/meetings/Test Meeting/removePerson")
                .param("person", "John Doe"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Person not found"));
    }
}
