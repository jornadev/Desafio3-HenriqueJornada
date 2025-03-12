package com.compass.controller;

import com.compass.dto.EventRequest;
import com.compass.dto.EventDTO;
import com.compass.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping("/create-event")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventRequest request) {
        EventDTO createdEvent = eventService.createEvent(request);
        return ResponseEntity.ok(createdEvent);
    }

    @GetMapping("/get-event/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable String id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/get-all-events/sorted")
    public ResponseEntity<List<EventDTO>> getAllEventsSorted() {
        return ResponseEntity.ok(eventService.getAllEventsSorted());
    }

    @PutMapping("/update-event/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable String id, @RequestBody EventRequest request) {
        return eventService.updateEvent(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete-event/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
