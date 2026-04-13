package com.iict.buet.customer_portal.client.dbbl.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class GetransidDto {
    @Min(value = 1, message = "card type value can't be less than 1")
    @Max(value = 6, message = "card type value can't be greater than 6")
    private Integer cardtype;
}
