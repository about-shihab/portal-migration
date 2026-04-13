package com.iict.buet.customer_portal.dto;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CustomerBillDto {
    private String billMonth;
    private Long billYear;
    private String status;
    private BigDecimal billAmount;
    private BigDecimal surcharge;
    private BigDecimal previousSurcharge;
    private BigDecimal meterRent;
    private BigDecimal applianceQty;
    private Boolean isNonMetered;
    private BigDecimal currentTotal;
    private String lastDateOfPayment;
}
