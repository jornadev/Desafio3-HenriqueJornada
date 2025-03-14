package com.compass.controller;

import com.compass.dto.TicketRequest;
import com.compass.dto.TicketResponse;
import com.compass.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private TicketRequest ticketRequest;
    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();

        ticketRequest = new TicketRequest();
        ticketRequest.setCustomerName("John Doe");
        ticketRequest.setCpf("12345678900");
        ticketRequest.setCustomerMail("john.doe@example.com");
        ticketRequest.setEventId("E123");
        ticketRequest.setBrlAmount(100.0);
        ticketRequest.setUsdAmount(20.0);

        ticketResponse = new TicketResponse();
        ticketResponse.setTicketId("T123");
        ticketResponse.setCustomerName("John Doe");
        ticketResponse.setBRLtotalAmount("100.0");
        ticketResponse.setUSDtotalAmount("20.0");
    }
    
    @Test
    void testGetTicket() throws Exception {
        when(ticketService.getTicket("T123")).thenReturn(ticketResponse);

        mockMvc.perform(get("/api/tickets/get-ticket/T123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value("T123"))
                .andExpect(jsonPath("$.customerName").value("John Doe"));

        verify(ticketService, times(1)).getTicket("T123");
    }

    @Test
    void testUpdateTicket() throws Exception {
        when(ticketService.updateTicket("T123", ticketRequest)).thenReturn(ticketResponse);

        mockMvc.perform(put("/api/tickets/update-ticket/T123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"John Doe\",\"cpf\":\"12345678900\",\"customerMail\":\"john.doe@example.com\",\"eventId\":\"E123\",\"brlAmount\":100.0,\"usdAmount\":20.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value("T123"))
                .andExpect(jsonPath("$.customerName").value("John Doe"));

        verify(ticketService, times(1)).updateTicket(eq("T123"), any(TicketRequest.class));
    }

    @Test
    void testCancelTicket() throws Exception {
        when(ticketService.cancelTicket("T123")).thenReturn("Ticket cancelado com sucesso");

        mockMvc.perform(delete("/api/tickets/cancel-ticket/T123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ticket cancelado com sucesso"));

        verify(ticketService, times(1)).cancelTicket("T123");
    }

    @Test
    void testCheckTicketsByEvent() throws Exception {
        when(ticketService.getTicketsByEvent("E123")).thenReturn(List.of(ticketResponse));

        mockMvc.perform(get("/api/tickets/check-tickets-by-event/E123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketId").value("T123"))
                .andExpect(jsonPath("$[0].customerName").value("John Doe"));

        verify(ticketService, times(1)).getTicketsByEvent("E123");
    }
}
