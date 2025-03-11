package com.compass.controller;

import com.compass.dto.TicketRequest;
import com.compass.dto.TicketResponse;
import com.compass.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/create-ticket")
    public ResponseEntity<TicketResponse> createTicket(@RequestBody TicketRequest ticketRequest) {
        TicketResponse response = ticketService.createTicket(ticketRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get-ticket/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable String id) {
        TicketResponse response = ticketService.getTicket(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-ticket/{id}")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable String id, @RequestBody TicketRequest ticketRequest) {
        TicketResponse response = ticketService.updateTicket(id, ticketRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/cancel-ticket/{id}")
    public ResponseEntity<String> cancelTicket(@PathVariable String id) {
        String response = ticketService.cancelTicket(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/check-tickets-by-event/{eventId}")
    public ResponseEntity<List<TicketResponse>> checkTicketsByEvent(@PathVariable String eventId) {
        List<TicketResponse> response = ticketService.getTicketsByEvent(eventId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
