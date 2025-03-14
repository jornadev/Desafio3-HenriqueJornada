package com.compass.dto;

import lombok.Data;

@Data
public class TicketRequest {
    private String customerName;
    private String cpf;
    private String customerMail;
    private String eventId;
    private String eventName;
    private double brlAmount;
    private double usdAmount;
}
