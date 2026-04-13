package com.iict.buet.customer_portal.dto;

import lombok.Data;

@Data
public class TransactionDto {
    private String transactionRef;
    private String transactionType;
    private String transactionDate;
    private String transactionAmount;
    private String bankName;
    private String branchName;
}
