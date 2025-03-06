package com.compass.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRequest {
    private String eventName;
    private LocalDateTime dateTime;
    private String cep;
}
