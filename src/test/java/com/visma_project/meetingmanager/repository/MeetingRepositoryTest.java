package com.visma_project.meetingmanager.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visma_project.meetingmanager.model.Meeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MeetingRepositoryTest {

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private MeetingRepository meetingRepository;

    @TempDir
    Path tempDir;

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        tempFile = tempDir.resolve("meetings.json").toFile();
        meetingRepository.filePath = tempFile.getAbsolutePath(); // Set the file path to the temporary file
        when(mapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(new ArrayList<>());
        meetingRepository.init();
    }


    @Test
    void findAll_shouldReturnAllMeetings() {
        Meeting meeting1 = new Meeting();
        meeting1.setName("Meeting 1");
        Meeting meeting2 = new Meeting();
        meeting2.setName("Meeting 2");

        meetingRepository.save(meeting1);
        meetingRepository.save(meeting2);

        assertEquals(2, meetingRepository.findAll().size());
    }

    @Test
    void save_shouldAddMeetingAndPersist() throws IOException {
        Meeting meeting = new Meeting();
        meeting.setName("Test Meeting");

        meetingRepository.save(meeting);

        assertTrue(meetingRepository.findAll().contains(meeting));
        verify(mapper, times(2)).writeValue(eq(new File(meetingRepository.filePath)), anyList());
    }

    @Test
    void delete_shouldRemoveMeetingAndPersist() throws IOException {
        Meeting meeting = new Meeting();
        meeting.setName("Test Meeting");
        meetingRepository.save(meeting);

        meetingRepository.delete(meeting);

        assertFalse(meetingRepository.findAll().contains(meeting));
        verify(mapper, times(3)).writeValue(eq(new File(meetingRepository.filePath)), anyList());
    }

    @Test
    void findById_shouldReturnMeetingWhenPresent() {
        Meeting meeting = new Meeting();
        meeting.setName("Test Meeting");
        meetingRepository.save(meeting);

        Optional<Meeting> foundMeeting = meetingRepository.findById("Test Meeting");

        assertTrue(foundMeeting.isPresent());
        assertEquals(meeting, foundMeeting.get());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotPresent() {
        Optional<Meeting> foundMeeting = meetingRepository.findById("Nonexistent Meeting");

        assertFalse(foundMeeting.isPresent());
    }
}
