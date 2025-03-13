package com.compass.service;

import com.compass.client.ViaCepClient;
import com.compass.client.FeignClientClient;
import com.compass.dto.EventRequest;
import com.compass.dto.EventDTO;
import com.compass.dto.TicketResponse;
import com.compass.exception.*;
import com.compass.model.Address;
import com.compass.model.Event;
import com.compass.repository.EventRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ViaCepClient viaCepClient;
    private final FeignClientClient feignClientClient;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    public EventDTO createEvent(EventRequest request) {
        try {
            Address address = viaCepClient.getAddress(request.getCep());
            if (address == null || address.getLogradouro() == null) {
                throw new InvalidCepException("CEP inválido ou não encontrado.");
            }
            Event event = new Event(UUID.randomUUID().toString(), request.getEventName(), request.getDateTime(), address);
            Event savedEvent = eventRepository.save(event);
            return mapToDTO(savedEvent);
        } catch (FeignException ex) {
            throw new InvalidCepException("Erro ao buscar o endereço pelo CEP.");
        }
    }

    public Optional<EventDTO> getEventById(String id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new EventNotFoundException("Evento com ID " + id + " não encontrado.");
        }
        return event.map(this::mapToDTO);
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
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new EventNotFoundException("Evento com ID " + id + " não encontrado.");
        }
        try {
            Event existingEvent = event.get();
            existingEvent.setEventName(request.getEventName());
            existingEvent.setDateTime(request.getDateTime());
            Address updatedAddress = viaCepClient.getAddress(request.getCep());
            if (updatedAddress == null || updatedAddress.getLogradouro() == null) {
                throw new InvalidCepException("CEP inválido ou não encontrado.");
            }
            existingEvent.setAddress(updatedAddress);
            Event updatedEvent = eventRepository.save(existingEvent);
            return Optional.of(mapToDTO(updatedEvent));
        } catch (FeignException ex) {
            throw new InvalidCepException("Erro ao buscar o endereço pelo CEP.");
        } catch (Exception ex) {
            throw new EventUpdateException("Erro ao atualizar o evento com ID: " + id);
        }
    }

    public void deleteEvent(String id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new EventNotFoundException("Evento com ID " + id + " não encontrado.");
        }
        try {
            List<TicketResponse> tickets = feignClientClient.checkTicketsByEvent(id);
            if (tickets != null && !tickets.isEmpty()) {
                throw new EventHasTicketsException("Você não pode apagar este evento porque há tickets vendidos!");
            }
        } catch (FeignException ex) {
            logger.warn("Erro ao verificar ingressos para o evento ID: {}", id);
        }

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
