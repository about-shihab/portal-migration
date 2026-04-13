package com.iict.buet.customer_portal.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentInfoDto {
    private String paymentRef;
    private BigDecimal totalAmount;
}