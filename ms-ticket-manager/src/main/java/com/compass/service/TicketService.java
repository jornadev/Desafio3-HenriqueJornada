package com.compass.service;

import com.compass.dto.EventDTO;
import com.compass.dto.TicketRequest;
import com.compass.dto.TicketResponse;
import com.compass.model.Ticket;
import com.compass.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    private static final String EVENT_SERVICE_URL = "http://localhost:8081/events/get-event/{id}";

    @Autowired
    private RestTemplate restTemplate;

    public TicketResponse createTicket(TicketRequest ticketRequest) {
        String eventId = ticketRequest.getEventId();
        EventDTO event = restTemplate.getForObject(EVENT_SERVICE_URL, EventDTO.class, eventId);

        if (event == null) {
            throw new RuntimeException("Evento não encontrado");
        }

        // Criar o Ticket
        Ticket ticket = new Ticket();
        ticket.setCustomerName(ticketRequest.getCustomerName());
        ticket.setCpf(ticketRequest.getCpf());
        ticket.setCustomerMail(ticketRequest.getCustomerMail());
        ticket.setEventId(ticketRequest.getEventId());
        ticket.setEventName(ticketRequest.getEventName());
        ticket.setBRLamount(String.valueOf(ticketRequest.getBrlAmount()));
        ticket.setUSDamount(String.valueOf(ticketRequest.getUsdAmount()));

        Ticket savedTicket = ticketRepository.save(ticket);

        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setTicketId(savedTicket.getTicketId());
        ticketResponse.setCpf(savedTicket.getCpf());
        ticketResponse.setCustomerName(savedTicket.getCustomerName());
        ticketResponse.setCustomerMail(savedTicket.getCustomerMail());
        ticketResponse.setEvent(event);
        ticketResponse.setBRLtotalAmount(savedTicket.getBRLamount());
        ticketResponse.setUSDtotalAmount(savedTicket.getUSDamount());
        ticketResponse.setStatus("Criado com sucesso");

        return ticketResponse;
    }

    public TicketResponse getTicket(String id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            throw new RuntimeException("Ingresso não encontrado");
        }

        TicketResponse ticketResponse = new TicketResponse();
        Ticket t = ticket.get();
        ticketResponse.setTicketId(t.getTicketId());
        ticketResponse.setCpf(t.getCpf());
        ticketResponse.setCustomerName(t.getCustomerName());
        ticketResponse.setCustomerMail(t.getCustomerMail());

        EventDTO event = restTemplate.getForObject(EVENT_SERVICE_URL + "/{eventId}", EventDTO.class, t.getEventId());
        ticketResponse.setEvent(event);
        ticketResponse.setBRLtotalAmount(t.getBRLamount());
        ticketResponse.setUSDtotalAmount(t.getUSDamount());
        ticketResponse.setStatus("Ativo");

        return ticketResponse;
    }

    public TicketResponse updateTicket(String id, TicketRequest ticketRequest) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            throw new RuntimeException("Ingresso não encontrado");
        }

        Ticket t = ticket.get();
        t.setCustomerName(ticketRequest.getCustomerName());
        t.setCpf(ticketRequest.getCpf());
        t.setCustomerMail(ticketRequest.getCustomerMail());
        t.setEventId(ticketRequest.getEventId());
        t.setEventName(ticketRequest.getEventName());
        t.setBRLamount(String.valueOf(ticketRequest.getBrlAmount()));
        t.setUSDamount(String.valueOf(ticketRequest.getUsdAmount()));

        Ticket updatedTicket = ticketRepository.save(t);

        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setTicketId(updatedTicket.getTicketId());
        ticketResponse.setCpf(updatedTicket.getCpf());
        ticketResponse.setCustomerName(updatedTicket.getCustomerName());
        ticketResponse.setCustomerMail(updatedTicket.getCustomerMail());

        EventDTO event = restTemplate.getForObject(EVENT_SERVICE_URL + "/{eventId}", EventDTO.class, t.getEventId());
        ticketResponse.setEvent(event);
        ticketResponse.setBRLtotalAmount(updatedTicket.getBRLamount());
        ticketResponse.setUSDtotalAmount(updatedTicket.getUSDamount());
        ticketResponse.setStatus("Atualizado com sucesso");

        return ticketResponse;
    }

    public String cancelTicket(String id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            throw new RuntimeException("Ingresso não encontrado");
        }

        Ticket t = ticket.get();
        ticketRepository.delete(t);

        return "Ingresso cancelado com sucesso";
    }

    public List<TicketResponse> getTicketsByEvent(String eventId) {
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        List<TicketResponse> ticketResponses = new ArrayList<>();

        for (Ticket ticket : tickets) {
            TicketResponse response = new TicketResponse();
            response.setTicketId(ticket.getTicketId());
            response.setCpf(ticket.getCpf());
            response.setCustomerName(ticket.getCustomerName());
            response.setCustomerMail(ticket.getCustomerMail());
            EventDTO event = restTemplate.getForObject(EVENT_SERVICE_URL + "/{eventId}", EventDTO.class, ticket.getEventId());
            response.setEvent(event);
            response.setBRLtotalAmount(ticket.getBRLamount());
            response.setUSDtotalAmount(ticket.getUSDamount());
            response.setStatus("Ativo");

            ticketResponses.add(response);
        }

        return ticketResponses;
    }
}
