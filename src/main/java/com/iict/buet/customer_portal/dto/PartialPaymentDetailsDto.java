package com.iict.buet.customer_portal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartialPaymentDetailsDto {
    private int billYear;
    private int billMonth;
    private String custCode;
    private int installmentNo;
    private String status;
    private double partialAmount;
    @JsonFormat(pattern = "dd-MMM-yyyy")
    private LocalDate lastDateOfPayment;
}