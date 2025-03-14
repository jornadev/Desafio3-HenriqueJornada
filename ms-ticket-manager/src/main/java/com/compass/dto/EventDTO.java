package com.compass.dto;

import lombok.Data;

@Data
public class EventDTO {
    private String eventId;
    private String eventName;
    private String eventDateTime;
    private String logradouro;
    private String bairro;
    private String cidade;
    private String uf;
}
