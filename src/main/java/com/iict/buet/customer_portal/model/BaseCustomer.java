package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@MappedSuperclass
@Data
public abstract class BaseCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    @Column(name = "PRE_CUST_CODE")
    private String oldCode;
    @Column(name = "IS_REGISTERED")
    private Boolean isRegistered;
    private String email;
    private String zone;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CONNECTION_STATUS_ID", referencedColumnName = "id")
    private ConnectionStatusType connectionStatusType;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUST_TYPE_ID", referencedColumnName = "id")
    private CustomerType customerType;
}
