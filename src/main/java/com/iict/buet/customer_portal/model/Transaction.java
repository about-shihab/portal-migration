package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "transaction")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "TRANSACTION_REF")
    private String transactionRef;
    @Column(name = "TRANSACTION_TOTAL")
    private BigDecimal transactionTotal;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TRANSACTION_DATE")
    private Date transactionDate;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TRANSACTION_TYPE", referencedColumnName = "id")
    private TransactionType transactionType;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TRANSACTION_CHANNEL", referencedColumnName = "id")
    private Bank bank;
    @Column(name = "CUST_CODE")
    private String customerCode;
    @Column(name = "CHANNEL_BRANCH")
    private String branchName;

}
