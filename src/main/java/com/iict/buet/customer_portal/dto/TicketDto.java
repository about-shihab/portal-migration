package com.iict.buet.customer_portal.dto;

import lombok.Data;

@Data
public class TicketDto {
    private String complainDescription;
    private String ticketNo;
    private String status;
    private String complainDate;
}
