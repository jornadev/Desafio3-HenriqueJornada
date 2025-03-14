package com.compass.service;

import com.compass.client.FeignClientClient;
import com.compass.client.ViaCepClient;
import com.compass.dto.EventRequest;
import com.compass.dto.EventDTO;
import com.compass.exception.EventNotFoundException;
import com.compass.exception.InvalidCepException;
import com.compass.model.Address;
import com.compass.model.Event;
import com.compass.repository.EventRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ViaCepClient viaCepClient;

    @Mock
    private FeignClientClient feignClientClient;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventService(eventRepository, viaCepClient, feignClientClient);
    }

    @Test
    void testCreateEvent_Success() {
        EventRequest request = new EventRequest();
        request.setEventName("Concert");
        request.setDateTime(null);
        request.setCep("12345678");

        Address address = new Address();
        address.setLogradouro("Main St");
        address.setLocalidade("City");
        address.setBairro("Downtown");
        address.setUf("ST");

        when(viaCepClient.getAddress(anyString())).thenReturn(address);
        when(eventRepository.save(any(Event.class))).thenReturn(new Event("1", "Concert", null, address));

        EventDTO createdEvent = eventService.createEvent(request);

        assertNotNull(createdEvent);
        assertEquals("Concert", createdEvent.getEventName());
    }

    @Test
    void testCreateEvent_InvalidCep() {
        EventRequest request = new EventRequest();
        request.setEventName("Concert");
        request.setDateTime(null);
        request.setCep("12345678");

        when(viaCepClient.getAddress(anyString())).thenReturn(null);

        assertThrows(InvalidCepException.class, () -> eventService.createEvent(request));
    }

    @Test
    void testGetEventById_Success() {
        Event event = new Event("1", "Concert", null, new Address());
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));

        Optional<EventDTO> result = eventService.getEventById("1");

        assertTrue(result.isPresent());
        assertEquals("Concert", result.get().getEventName());
    }

    @Test
    void testGetEventById_NotFound() {
        when(eventRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getEventById("1"));
    }

    @Test
    void testDeleteEvent_Success() {
        Event event = new Event("1", "Concert", null, new Address());
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));
        when(feignClientClient.checkTicketsByEvent(anyString())).thenReturn(List.of());

        eventService.deleteEvent("1");

        verify(eventRepository, times(1)).deleteById("1");
    }

    @Test
    void testUpdateEvent_Success() {
        EventRequest request = new EventRequest();
        request.setEventName("Updated Concert");
        request.setDateTime(null);
        request.setCep("12345678");

        Address address = new Address();
        address.setLogradouro("Updated St");
        address.setLocalidade("Updated City");
        address.setBairro("Updated Downtown");
        address.setUf("UP");

        Event existingEvent = new Event("1", "Concert", null, new Address());
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(existingEvent));
        when(viaCepClient.getAddress(anyString())).thenReturn(address);
        when(eventRepository.save(any(Event.class))).thenReturn(new Event("1", "Updated Concert", null, address));

        Optional<EventDTO> updatedEvent = eventService.updateEvent("1", request);

        assertTrue(updatedEvent.isPresent());
        assertEquals("Updated Concert", updatedEvent.get().getEventName());
    }

    @Test
    void testUpdateEvent_EventNotFound() {
        EventRequest request = new EventRequest();
        request.setEventName("Updated Concert");
        request.setDateTime(null);
        request.setCep("12345678");

        when(eventRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent("1", request));
    }

    @Test
    void testDeleteEvent_FeignException() {
        Event event = new Event("1", "Concert", null, new Address());
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));
        when(feignClientClient.checkTicketsByEvent(anyString())).thenThrow(FeignException.class);

        assertDoesNotThrow(() -> eventService.deleteEvent("1"));
    }
    @Test
    void testGetAllEvents_Success() {
        Event event1 = new Event("1", "Concert", null, new Address());
        Event event2 = new Event("2", "Theater", null, new Address());
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<EventDTO> events = eventService.getAllEvents();

        assertNotNull(events);
        assertEquals(2, events.size());
    }
    @Test
    void testGetAllEventsSorted_Success() {
        Event event1 = new Event("1", "Theater", null, new Address());
        Event event2 = new Event("2", "Concert", null, new Address());
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<EventDTO> sortedEvents = eventService.getAllEventsSorted();

        assertNotNull(sortedEvents);
        assertEquals("Concert", sortedEvents.get(0).getEventName());
        assertEquals("Theater", sortedEvents.get(1).getEventName());
    }
    @Test
    void testCreateEvent_FeignException() {
        EventRequest request = new EventRequest();
        request.setEventName("Concert");
        request.setDateTime(null);
        request.setCep("12345678");

        when(viaCepClient.getAddress(anyString())).thenThrow(FeignException.class);

        assertThrows(InvalidCepException.class, () -> eventService.createEvent(request));
    }
    @Test
    void testDeleteEvent_NoTickets() {
        Event event = new Event("1", "Concert", null, new Address());
        when(eventRepository.findById(anyString())).thenReturn(Optional.of(event));
        when(feignClientClient.checkTicketsByEvent(anyString())).thenReturn(List.of());

        eventService.deleteEvent("1");

        verify(eventRepository, times(1)).deleteById("1");
    }

}
