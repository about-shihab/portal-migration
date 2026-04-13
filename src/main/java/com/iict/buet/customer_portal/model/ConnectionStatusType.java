package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "CONNECTION_STATUS_TYPE")
public class ConnectionStatusType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "CONNECTION_STATUS")
    private String name;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
}
