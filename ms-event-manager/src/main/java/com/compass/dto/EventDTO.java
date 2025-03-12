package com.compass.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDTO {
    private String eventId;
    private String eventName;
    private LocalDateTime eventDateTime;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
}
