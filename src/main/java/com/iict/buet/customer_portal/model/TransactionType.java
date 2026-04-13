package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "transaction_type")
@Data
public class TransactionType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "TRANSACTION_TYPE_NAME")
    private String name;
}
