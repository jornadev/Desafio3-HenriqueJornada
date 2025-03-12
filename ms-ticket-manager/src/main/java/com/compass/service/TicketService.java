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

    // Método para criar um ticket
    public TicketResponse createTicket(TicketRequest ticketRequest) {
        String eventId = ticketRequest.getEventId();
        // Recupera os dados do evento usando o eventId
        EventDTO event = restTemplate.getForObject(EVENT_SERVICE_URL, EventDTO.class, eventId);

        if (event == null) {
            throw new RuntimeException("Evento não encontrado");
        }

        // Criação do Ticket com eventId
        Ticket ticket = new Ticket();
        ticket.setCustomerName(ticketRequest.getCustomerName());
        ticket.setCpf(ticketRequest.getCpf());
        ticket.setCustomerMail(ticketRequest.getCustomerMail());
        ticket.setEventId(ticketRequest.getEventId()); // Armazenando apenas o eventId
        ticket.setBRLtotalAmount(String.valueOf(ticketRequest.getBrlAmount()));  // Mantendo como String
        ticket.setUSDtotalAmount(String.valueOf(ticketRequest.getUsdAmount()));  // Mantendo como String

        Ticket savedTicket = ticketRepository.save(ticket);

        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setTicketId(savedTicket.getTicketId());
        ticketResponse.setCpf(savedTicket.getCpf());
        ticketResponse.setCustomerName(savedTicket.getCustomerName());
        ticketResponse.setCustomerMail(savedTicket.getCustomerMail());
        ticketResponse.setEvent(event);  // Incluindo os dados do evento na resposta
        ticketResponse.setBRLtotalAmount(savedTicket.getBRLtotalAmount());
        ticketResponse.setUSDtotalAmount(savedTicket.getUSDtotalAmount());
        ticketResponse.setStatus("Criado com sucesso");

        return ticketResponse;
    }

    // Método para obter um ticket por ID
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

        // Recupera o evento associado usando o eventId do ticket
        EventDTO event = restTemplate.getForObject(EVENT_SERVICE_URL, EventDTO.class, t.getEventId());
        ticketResponse.setEvent(event);
        ticketResponse.setBRLtotalAmount(t.getBRLtotalAmount());
        ticketResponse.setUSDtotalAmount(t.getUSDtotalAmount());
        ticketResponse.setStatus("Ativo");

        return ticketResponse;
    }

    // Método para atualizar um ticket
    public TicketResponse updateTicket(String id, TicketRequest ticketRequest) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            throw new RuntimeException("Ingresso não encontrado");
        }

        Ticket t = ticket.get();
        t.setCustomerName(ticketRequest.getCustomerName());
        t.setCpf(ticketRequest.getCpf());
        t.setCustomerMail(ticketRequest.getCustomerMail());
        t.setEventId(ticketRequest.getEventId()); // Atualiza apenas o eventId
        t.setBRLtotalAmount(String.valueOf(ticketRequest.getBrlAmount()));  // Mantendo como String
        t.setUSDtotalAmount(String.valueOf(ticketRequest.getUsdAmount()));  // Mantendo como String

        Ticket updatedTicket = ticketRepository.save(t);

        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setTicketId(updatedTicket.getTicketId());
        ticketResponse.setCpf(updatedTicket.getCpf());
        ticketResponse.setCustomerName(updatedTicket.getCustomerName());
        ticketResponse.setCustomerMail(updatedTicket.getCustomerMail());

        // Recupera o evento associado ao ticket usando o eventId
        EventDTO event = restTemplate.getForObject(EVENT_SERVICE_URL, EventDTO.class, t.getEventId());
        ticketResponse.setEvent(event);
        ticketResponse.setBRLtotalAmount(updatedTicket.getBRLtotalAmount());
        ticketResponse.setUSDtotalAmount(updatedTicket.getUSDtotalAmount());
        ticketResponse.setStatus("Atualizado com sucesso");

        return ticketResponse;
    }

    // Método para cancelar um ticket
    public String cancelTicket(String id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            throw new RuntimeException("Ingresso não encontrado");
        }

        Ticket t = ticket.get();
        ticketRepository.delete(t);

        return "Ingresso cancelado com sucesso";
    }

    // Método para obter todos os tickets de um evento
    public List<TicketResponse> getTicketsByEvent(String eventId) {
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        List<TicketResponse> ticketResponses = new ArrayList<>();

        for (Ticket ticket : tickets) {
            TicketResponse response = new TicketResponse();
            response.setTicketId(ticket.getTicketId());
            response.setCpf(ticket.getCpf());
            response.setCustomerName(ticket.getCustomerName());
            response.setCustomerMail(ticket.getCustomerMail());

            // Recupera o evento associado ao ticket usando o eventId
            EventDTO event = restTemplate.getForObject(EVENT_SERVICE_URL, EventDTO.class, ticket.getEventId());
            response.setEvent(event);
            response.setBRLtotalAmount(ticket.getBRLtotalAmount());
            response.setUSDtotalAmount(ticket.getUSDtotalAmount());
            response.setStatus("Ativo");

            ticketResponses.add(response);
        }

        return ticketResponses;
    }
}
