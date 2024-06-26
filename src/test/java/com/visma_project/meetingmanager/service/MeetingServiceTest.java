package com.visma_project.meetingmanager.service;

import com.visma_project.meetingmanager.model.Category;
import com.visma_project.meetingmanager.model.Meeting;
import com.visma_project.meetingmanager.model.Type;
import com.visma_project.meetingmanager.repository.MeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetingServiceTest {

    @Mock
    private MeetingRepository repository;

    @InjectMocks
    private MeetingService meetingService;

    private Meeting meeting;

    @BeforeEach
    public void setUp() {
        meeting = new Meeting();
        meeting.setName("Team Meeting");
        meeting.setResponsiblePerson("Alice");
        meeting.setDescription("Discuss project progress");
        meeting.setCategory(Category.TeamBuilding);
        meeting.setType(Type.Live);
        meeting.setStartDate(LocalDateTime.of(2023, 6, 1, 10, 0));
        meeting.setEndDate(LocalDateTime.of(2023, 6, 1, 11, 0));
        meeting.setAttendees(new HashSet<>(Collections.singleton("Alice")));
    }

    @Test
    public void testCreateMeeting() {
        meetingService.createMeeting(meeting);
        verify(repository, times(1)).save(meeting);
    }

    @Test
    public void testDeleteMeetingSuccess() {
        when(repository.findById("Team Meeting")).thenReturn(Optional.of(meeting));
        meetingService.deleteMeeting("Team Meeting", "Alice");
        verify(repository, times(1)).delete(meeting);
    }

    @Test
    public void testDeleteMeetingFailure() {
        when(repository.findById("Team Meeting")).thenReturn(Optional.of(meeting));
        assertThrows(IllegalArgumentException.class, () -> {
            meetingService.deleteMeeting("Team Meeting", "Bob");
        });
        verify(repository, times(0)).delete(meeting);
    }

    @Test
    public void testAddPersonToMeetingSuccess() {
        when(repository.findById("Team Meeting")).thenReturn(Optional.of(meeting));
        when(repository.findAll()).thenReturn(Collections.singletonList(meeting));

        meetingService.addPersonToMeeting("Team Meeting", "Bob", LocalDateTime.of(2023, 6, 1, 10, 0));
        assertTrue(meeting.getAttendees().contains("Bob"));
    }

    @Test
    public void testAddPersonToMeetingConflict() {
        Meeting anotherMeeting = new Meeting();
        anotherMeeting.setName("Another Meeting");
        anotherMeeting.setResponsiblePerson("Bob");
        anotherMeeting.setDescription("Discuss another project");
        anotherMeeting.setCategory(Category.CodeMonkey);
        anotherMeeting.setType(Type.InPerson);
        anotherMeeting.setStartDate(LocalDateTime.of(2023, 6, 1, 9, 30));
        anotherMeeting.setEndDate(LocalDateTime.of(2023, 6, 1, 11, 30));
        anotherMeeting.setAttendees(new HashSet<>(Collections.singleton("Bob")));

        when(repository.findById("Team Meeting")).thenReturn(Optional.of(meeting));
        when(repository.findAll()).thenReturn(Arrays.asList(meeting, anotherMeeting));

        assertThrows(IllegalArgumentException.class, () -> {
        meetingService.addPersonToMeeting("Team Meeting", "Bob", LocalDateTime.of(2023, 6, 1, 10, 0));
    });
}


    @Test
    public void testRemovePersonFromMeetingSuccess() {
        meeting.getAttendees().add("Bob");
        when(repository.findById("Team Meeting")).thenReturn(Optional.of(meeting));

        meetingService.removePersonFromMeeting("Team Meeting", "Bob");
        assertFalse(meeting.getAttendees().contains("Bob"));
    }

    @Test
    public void testRemovePersonFromMeetingFailure() {
        when(repository.findById("Team Meeting")).thenReturn(Optional.of(meeting));

        assertThrows(IllegalArgumentException.class, () -> {
            meetingService.removePersonFromMeeting("Team Meeting", "Alice");
        });
    }

    @Test
    public void testFilterMeetings() {
        when(repository.findAll()).thenReturn(Collections.singletonList(meeting));

        List<Meeting> result = meetingService.filterMeetings("project", null, null, null, null, null, null);
        assertEquals(1, result.size());

        result = meetingService.filterMeetings(null, "Alice", null, null, null, null, null);
        assertEquals(1, result.size());

        result = meetingService.filterMeetings(null, null, Category.TeamBuilding, null, null, null, null);
        assertEquals(1, result.size());

        result = meetingService.filterMeetings(null, null, null, Type.Live, null, null, null);
        assertEquals(1, result.size());

        result = meetingService.filterMeetings(null, null, null, null, LocalDateTime.of(2023, 5, 1, 0, 0), null, null);
        assertEquals(1, result.size());

        result = meetingService.filterMeetings(null, null, null, null, null, LocalDateTime.of(2023, 7, 1, 0, 0), null);
        assertEquals(1, result.size());

        result = meetingService.filterMeetings(null, null, null, null, null, null, 1);
        assertEquals(1, result.size());
    }
}
