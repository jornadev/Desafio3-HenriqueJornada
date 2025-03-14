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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private FeignClientConfig feignClientConfig;

    @InjectMocks
    private TicketService ticketService;

    private TicketRequest ticketRequest;
    private Ticket ticket;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        ticketRequest = new TicketRequest();
        ticketRequest.setCustomerName("John Doe");
        ticketRequest.setCpf("12345678900");
        ticketRequest.setCustomerMail("john.doe@example.com");
        ticketRequest.setEventId("E123");
        ticketRequest.setBrlAmount(100.0);
        ticketRequest.setUsdAmount(20.0);

        eventDTO = new EventDTO();
        eventDTO.setEventId("E123");
        eventDTO.setEventName("Concert");

        ticket = new Ticket();
        ticket.setTicketId("T123");
        ticket.setCustomerName("John Doe");
        ticket.setCpf("12345678900");
        ticket.setCustomerMail("john.doe@example.com");
        ticket.setEventId("E123");
        ticket.setBRLtotalAmount("100.0");
        ticket.setUSDtotalAmount("20.0");
    }

    @Test
    void testCreateTicket_Success() {
        when(feignClientConfig.getEventById("E123")).thenReturn(eventDTO);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        TicketResponse response = ticketService.createTicket(ticketRequest);

        assertNotNull(response);
        assertEquals("John Doe", response.getCustomerName());
        assertEquals("100.0", response.getBRLtotalAmount());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void testCreateTicket_EventNotFound() {
        when(feignClientConfig.getEventById("E123")).thenReturn(null);

        assertThrows(EventNotFoundException.class, () -> ticketService.createTicket(ticketRequest));
    }

    @Test
    void testGetTicket_Success() {
        when(ticketRepository.findById("T123")).thenReturn(Optional.of(ticket));
        when(feignClientConfig.getEventById("E123")).thenReturn(eventDTO);

        TicketResponse response = ticketService.getTicket("T123");

        assertNotNull(response);
        assertEquals("John Doe", response.getCustomerName());
        verify(ticketRepository, times(1)).findById("T123");
    }

    @Test
    void testGetTicket_NotFound() {
        when(ticketRepository.findById("T123")).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.getTicket("T123"));
    }

    @Test
    void testUpdateTicket_Success() {
        when(ticketRepository.findById("T123")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(feignClientConfig.getEventById("E123")).thenReturn(eventDTO);

        TicketResponse response = ticketService.updateTicket("T123", ticketRequest);

        assertNotNull(response);
        assertEquals("Atualizado com sucesso", response.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void testUpdateTicket_NotFound() {
        when(ticketRepository.findById("T123")).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.updateTicket("T123", ticketRequest));
    }
    @Test
    void testUpdateTicket_FailToRetrieveEvent() {
        when(ticketRepository.findById("T123")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(feignClientConfig.getEventById("E123")).thenThrow(new FeignClientException("Erro ao recuperar evento"));

        assertThrows(FeignClientException.class, () -> ticketService.updateTicket("T123", ticketRequest));
    }

    @Test
    void testCancelTicket_NotFound() {
        when(ticketRepository.findById("T123")).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.cancelTicket("T123"));
    }
    @Test
    void testGetTicket_FeignClientException() {
        when(ticketRepository.findById("T123")).thenReturn(Optional.of(ticket));
        when(feignClientConfig.getEventById("E123")).thenThrow(new FeignClientException("Erro de comunicação"));

        assertThrows(FeignClientException.class, () -> ticketService.getTicket("T123"));
    }
    @Test
    void testCreateTicket_FeignClientException() {
        when(feignClientConfig.getEventById("E123")).thenThrow(new FeignClientException("Erro de comunicação"));

        assertThrows(FeignClientException.class, () -> ticketService.createTicket(ticketRequest));
    }


    @Test
    void testCancelTicket_InvalidTicket() {
        when(ticketRepository.findById("T123")).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.cancelTicket("T123"));

    }
    @Test
    void testCancelTicket_Success() {
        when(ticketRepository.findById("T123")).thenReturn(Optional.of(ticket));

        String response = ticketService.cancelTicket("T123");

        assertEquals("Ingresso cancelado com sucesso", response);
        verify(ticketRepository, times(1)).delete(ticket);
    }

    @Test
    void testGetTicketsByEvent_Success() {
        when(ticketRepository.findByEventId("E123")).thenReturn(List.of(ticket));
        when(feignClientConfig.getEventById("E123")).thenReturn(eventDTO);

        List<TicketResponse> responses = ticketService.getTicketsByEvent("E123");

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("John Doe", responses.get(0).getCustomerName());
    }

    @Test
    void testGetTicketsByEvent_EventNotFound() {
        when(ticketRepository.findByEventId("E123")).thenReturn(List.of(ticket));
        when(feignClientConfig.getEventById("E123")).thenReturn(null);

        assertThrows(EventNotFoundException.class, () -> ticketService.getTicketsByEvent("E123"));
    }

}
