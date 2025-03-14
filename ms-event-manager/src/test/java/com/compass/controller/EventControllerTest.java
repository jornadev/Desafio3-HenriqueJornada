package com.compass.controller;

import com.compass.dto.EventRequest;
import com.compass.dto.EventDTO;
import com.compass.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventControllerTest {

    @Mock
    private EventService eventService;

    private EventController eventController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventController = new EventController(eventService);
    }

    @Test
    void testGetAllEvents_Success() {
        List<EventDTO> eventList = List.of(new EventDTO());
        when(eventService.getAllEvents()).thenReturn(eventList);

        ResponseEntity<List<EventDTO>> response = eventController.getAllEvents();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(eventList, response.getBody());
    }

    @Test
    void testCreateEvent_Success() {
        EventRequest request = new EventRequest();
        request.setEventName("Concert");
        request.setDateTime(null);
        request.setCep("12345678");

        EventDTO createdEvent = new EventDTO();
        createdEvent.setEventName("Concert");

        when(eventService.createEvent(request)).thenReturn(createdEvent);

        ResponseEntity<EventDTO> response = eventController.createEvent(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Concert", response.getBody().getEventName());
    }

    @Test
    void testGetEventById_Success() {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setEventName("Concert");

        when(eventService.getEventById("1")).thenReturn(Optional.of(eventDTO));

        ResponseEntity<EventDTO> response = eventController.getEventById("1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Concert", response.getBody().getEventName());
    }

    @Test
    void testGetEventById_NotFound() {
        when(eventService.getEventById("1")).thenReturn(Optional.empty());

        ResponseEntity<EventDTO> response = eventController.getEventById("1");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdateEvent_Success() {
        EventRequest request = new EventRequest();
        request.setEventName("Updated Concert");

        EventDTO updatedEvent = new EventDTO();
        updatedEvent.setEventName("Updated Concert");

        when(eventService.updateEvent("1", request)).thenReturn(Optional.of(updatedEvent));

        ResponseEntity<EventDTO> response = eventController.updateEvent("1", request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Concert", response.getBody().getEventName());
    }

    @Test
    void testDeleteEvent_Success() {
        doNothing().when(eventService).deleteEvent("1");

        ResponseEntity<Void> response = eventController.deleteEvent("1");

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
    }
}
