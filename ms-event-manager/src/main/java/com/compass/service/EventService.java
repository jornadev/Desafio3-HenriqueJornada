package com.compass.service;

import com.compass.client.ViaCepClient;
import com.compass.dto.EventRequest;
import com.compass.dto.EventDTO;
import com.compass.model.Address;
import com.compass.model.Event;
import com.compass.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ViaCepClient viaCepClient;

    public EventDTO createEvent(EventRequest request) {
        Address address = viaCepClient.getAddress(request.getCep());
        Event event = new Event(UUID.randomUUID().toString(), request.getEventName(), request.getDateTime(), address);
        Event savedEvent = eventRepository.save(event);
        return mapToDTO(savedEvent);
    }

    public Optional<EventDTO> getEventById(String id) {
        return eventRepository.findById(id).map(this::mapToDTO);
    }

    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream().map(this::mapToDTO).toList();
    }

    public List<EventDTO> getAllEventsSorted() {
        return eventRepository.findAll().stream()
                .sorted((e1, e2) -> e1.getEventName().compareToIgnoreCase(e2.getEventName()))
                .map(this::mapToDTO)
                .toList();
    }

    public Optional<EventDTO> updateEvent(String id, EventRequest request) {
        return eventRepository.findById(id).map(existingEvent -> {
            existingEvent.setEventName(request.getEventName());
            existingEvent.setDateTime(request.getDateTime());
            Address updatedAddress = viaCepClient.getAddress(request.getCep());
            existingEvent.setAddress(updatedAddress);
            Event updatedEvent = eventRepository.save(existingEvent);
            return mapToDTO(updatedEvent);
        });
    }

    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }

    private EventDTO mapToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setEventId(event.getId());
        dto.setEventName(event.getEventName());
        dto.setEventDateTime(event.getDateTime());
        dto.setLogradouro(event.getAddress().getLogradouro());
        dto.setBairro(event.getAddress().getBairro());
        dto.setCidade(event.getAddress().getLocalidade());
        dto.setUf(event.getAddress().getUf());
        return dto;
    }


}
