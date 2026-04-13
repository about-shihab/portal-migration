package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity(name = "TRANSACTION_CHANNEL_TYPE")
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "TRANSACTION_CHANNEL_TYPE_NAME")
    private String name;
    @Column(name = "NAME")
    private String fullName;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
    @Column(name = "API_USERNAME")
    private String username;
    private String type;
    @Column(name = "OWN_CARD_COST")
    private BigDecimal ownCardCost;
    @Column(name = "OTHER_CARD_COST")
    private BigDecimal otherCardCost;
    @Column(name = "DBBL_ROCKET_COST")
    private BigDecimal dbblRocketCost;
    @Column(name = "IS_MFS_OR_GATEWAY")
    private Boolean isMfsOrGateway;
}
