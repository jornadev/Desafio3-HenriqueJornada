package com.compass.controller;

import com.compass.dto.EventRequest;
import com.compass.dto.EventDTO;
import com.compass.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "API para gerenciamento de eventos")  // Nome e descrição da API no Swagger
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Listar todos os eventos", description = "Retorna uma lista com todos os eventos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @Operation(summary = "Criar um evento", description = "Cria um novo evento com os dados fornecidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping("/create-event")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventRequest request) {
        EventDTO createdEvent = eventService.createEvent(request);
        return ResponseEntity.ok(createdEvent);
    }

    @Operation(summary = "Buscar evento por ID", description = "Retorna os detalhes de um evento pelo ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/get-event/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable String id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar eventos ordenados", description = "Retorna uma lista de eventos ordenados por data ou outro critério.")
    @ApiResponse(responseCode = "200", description = "Lista de eventos ordenada retornada com sucesso")
    @GetMapping("/get-all-events/sorted")
    public ResponseEntity<List<EventDTO>> getAllEventsSorted() {
        return ResponseEntity.ok(eventService.getAllEventsSorted());
    }

    @Operation(summary = "Atualizar um evento", description = "Atualiza os dados de um evento existente pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @PutMapping("/update-event/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable String id, @RequestBody EventRequest request) {
        return eventService.updateEvent(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Excluir um evento", description = "Remove um evento pelo ID informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @DeleteMapping("/delete-event/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
