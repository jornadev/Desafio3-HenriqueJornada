package com.compass.controller;

import com.compass.dto.TicketRequest;
import com.compass.dto.TicketResponse;
import com.compass.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "API para gerenciamento de tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Operation(summary = "Cria um novo ticket", description = "cria um ticket baseado nos dados fornecidos no request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "ticket criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "dados inválidos")
    })
    @PostMapping("/create-ticket")
    public ResponseEntity<TicketResponse> createTicket(@RequestBody TicketRequest ticketRequest) {
        TicketResponse response = ticketService.createTicket(ticketRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtém um ticket pelo ID", description = "retorna os detalhes de um ticket específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ticket encontrado"),
            @ApiResponse(responseCode = "404", description = "ticket não encontrado")
    })
    @GetMapping("/get-ticket/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable String id) {
        TicketResponse response = ticketService.getTicket(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Atualiza um ticket", description = "atualiza os dados de um ticket existente pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ticket atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "ticket não encontrado")
    })
    @PutMapping("/update-ticket/{id}")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable String id, @RequestBody TicketRequest ticketRequest) {
        TicketResponse response = ticketService.updateTicket(id, ticketRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Cancela um ticket", description = "cancela um ticket com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ticket cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "ticket não encontrado")
    })
    @DeleteMapping("/cancel-ticket/{id}")
    public ResponseEntity<String> cancelTicket(@PathVariable String id) {
        String response = ticketService.cancelTicket(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Lista tickets de um evento", description = "retorna todos os tickets associados a um evento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "lista de tickets retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "evento não encontrado")
    })
    @GetMapping("/check-tickets-by-event/{eventId}")
    public ResponseEntity<List<TicketResponse>> checkTicketsByEvent(@PathVariable String eventId) {
        List<TicketResponse> response = ticketService.getTicketsByEvent(eventId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
