package com.compass.client;

import com.compass.dto.TicketResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Configuration
public class FeignClientClient {
    @FeignClient(name = "ms-ticket-manager", url = "http://localhost:8080/api/tickets/")
    public interface TicketServiceClient {
        @GetMapping("/check-tickets-by-event/{eventId}")
        List<TicketResponse> checkTicketsByEvent(@PathVariable String eventId);
    }

    private final TicketServiceClient ticketServiceClient;

    public FeignClientClient(TicketServiceClient ticketServiceClient) {
        this.ticketServiceClient = ticketServiceClient;
    }

    // verificar se existem tickets relacionados ao evento
    public List<TicketResponse> checkTicketsByEvent(String eventId) {
        return ticketServiceClient.checkTicketsByEvent(eventId);
    }
}
