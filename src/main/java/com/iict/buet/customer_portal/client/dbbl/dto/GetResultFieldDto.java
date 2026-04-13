package com.iict.buet.customer_portal.client.dbbl.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class GetResultFieldDto {
    @Size(min = 28, max = 28, message = "Transaction Id Size must be 28 character")
    private String transid;
}
