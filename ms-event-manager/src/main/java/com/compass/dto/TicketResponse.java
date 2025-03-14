package com.compass.dto;

import lombok.Data;
@Data
public class TicketResponse {

    private String ticketId;
    private String cpf;
    private String customerName;
    private String customerMail;
    private String eventId;
    private String BRLtotalAmount;
    private String USDtotalAmount;
    private String status;
}