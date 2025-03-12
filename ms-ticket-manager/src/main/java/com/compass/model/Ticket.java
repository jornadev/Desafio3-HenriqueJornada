package com.compass.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String ticketId;
    private String cpf;
    private String customerName;
    private String customerMail;
    private String eventId;
    private String status;
    private String BRLtotalAmount;
    private String USDtotalAmount;
}
