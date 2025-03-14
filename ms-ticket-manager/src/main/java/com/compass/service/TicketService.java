package com.compass.service;

import com.compass.config.FeignClientConfig;
import com.compass.dto.EventDTO;
import com.compass.dto.TicketRequest;
import com.compass.dto.TicketResponse;
import com.compass.exception.EventNotFoundException;
import com.compass.exception.FeignClientException;
import com.compass.exception.InvalidTicketDataException;
import com.compass.exception.TicketNotFoundException;
import com.compass.model.Ticket;
import com.compass.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private FeignClientConfig feignClientConfig;

    public TicketResponse createTicket(TicketRequest ticketRequest) {
        String eventId = ticketRequest.getEventId();
        EventDTO event = feignClientConfig.getEventById(eventId);
        if (event == null) {
            throw new EventNotFoundException("evento não encontrado");
        }
        Ticket ticket = new Ticket();
        ticket.setCustomerName(ticketRequest.getCustomerName());
        ticket.setCpf(ticketRequest.getCpf());
        ticket.setCustomerMail(ticketRequest.getCustomerMail());
        ticket.setEventId(ticketRequest.getEventId());
        ticket.setBRLtotalAmount(String.valueOf(ticketRequest.getBrlAmount()));
        ticket.setUSDtotalAmount(String.valueOf(ticketRequest.getUsdAmount()));

        Ticket savedTicket = ticketRepository.save(ticket);

        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setTicketId(savedTicket.getTicketId());
        ticketResponse.setCpf(savedTicket.getCpf());
        ticketResponse.setCustomerName(savedTicket.getCustomerName());
        ticketResponse.setCustomerMail(savedTicket.getCustomerMail());
        ticketResponse.setEvent(event);
        ticketResponse.setBRLtotalAmount(savedTicket.getBRLtotalAmount());
        ticketResponse.setUSDtotalAmount(savedTicket.getUSDtotalAmount());
        ticketResponse.setStatus("criado com sucesso");
        return ticketResponse;
    }

    public TicketResponse getTicket(String id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            throw new TicketNotFoundException("ingresso não encontrado");
        }

        TicketResponse ticketResponse = new TicketResponse();
        Ticket t = ticket.get();
        ticketResponse.setTicketId(t.getTicketId());
        ticketResponse.setCpf(t.getCpf());
        ticketResponse.setCustomerName(t.getCustomerName());
        ticketResponse.setCustomerMail(t.getCustomerMail());

        EventDTO event = feignClientConfig.getEventById(t.getEventId());
        if (event == null) {
            throw new EventNotFoundException("evento não encontrado.");
        }
        ticketResponse.setEvent(event);
        ticketResponse.setBRLtotalAmount(t.getBRLtotalAmount());
        ticketResponse.setUSDtotalAmount(t.getUSDtotalAmount());
        ticketResponse.setStatus("ativo");

        return ticketResponse;
    }

    public TicketResponse updateTicket(String id, TicketRequest ticketRequest) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            throw new TicketNotFoundException("ingresso não encontrado");
        }
        Ticket t = ticket.get();
        t.setCustomerName(ticketRequest.getCustomerName());
        t.setCpf(ticketRequest.getCpf());
        t.setCustomerMail(ticketRequest.getCustomerMail());
        t.setEventId(ticketRequest.getEventId());
        t.setBRLtotalAmount(String.valueOf(ticketRequest.getBrlAmount()));
        t.setUSDtotalAmount(String.valueOf(ticketRequest.getUsdAmount()));

        Ticket updatedTicket = ticketRepository.save(t);

        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setTicketId(updatedTicket.getTicketId());
        ticketResponse.setCpf(updatedTicket.getCpf());
        ticketResponse.setCustomerName(updatedTicket.getCustomerName());
        ticketResponse.setCustomerMail(updatedTicket.getCustomerMail());

        EventDTO event;
        try {
            event = feignClientConfig.getEventById(t.getEventId());
            if (event == null) {
                throw new EventNotFoundException("evento não encontrado.");
            }
        } catch (Exception e) {
            throw new FeignClientException("erro ao recuperar o evento: " + e.getMessage());
        }

        ticketResponse.setEvent(event);
        ticketResponse.setBRLtotalAmount(updatedTicket.getBRLtotalAmount());
        ticketResponse.setUSDtotalAmount(updatedTicket.getUSDtotalAmount());
        ticketResponse.setStatus("atualizado com sucesso");

        return ticketResponse;
    }

    public String cancelTicket(String id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isEmpty()) {
            throw new TicketNotFoundException("ingresso não encontrado");
        }

        Ticket t = ticket.get();
        ticketRepository.delete(t);
        return "ingresso cancelado com sucesso";
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
            EventDTO event = feignClientConfig.getEventById(ticket.getEventId());
            if (event == null) {
                throw new EventNotFoundException("evento não encontrado para o ticket.");
            }
            response.setEvent(event);
            response.setBRLtotalAmount(ticket.getBRLtotalAmount());
            response.setUSDtotalAmount(ticket.getUSDtotalAmount());
            response.setStatus("ativo");

            ticketResponses.add(response);
        }
        return ticketResponses;
    }
}
