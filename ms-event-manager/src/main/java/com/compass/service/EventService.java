package com.compass.service;

import com.compass.client.ViaCepClient;
import com.compass.dto.EventRequest;
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

    public Event createEvent(EventRequest request) {
        //System.out.println("requisitando CEP: " + request.getCep());
        Address address = viaCepClient.getAddress(request.getCep());
        Event event = new Event(UUID.randomUUID().toString(), request.getEventName(), request.getDateTime(), address);
        return eventRepository.save(event);
    }

    public Optional<Event> getEventById(String id) {
        return eventRepository.findById(id);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAllEventsSorted() {
        return eventRepository.findAll().stream()
                .sorted((e1, e2) -> e1.getEventName().compareToIgnoreCase(e2.getEventName()))
                .toList();
    }

    public Optional<Event> updateEvent(String id, EventRequest request) {
        return eventRepository.findById(id).map(existingEvent -> {
            existingEvent.setEventName(request.getEventName());
            existingEvent.setDateTime(request.getDateTime());
            existingEvent.setAddress(viaCepClient.getAddress(request.getCep()));
            return eventRepository.save(existingEvent);
        });
    }

    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }
}
