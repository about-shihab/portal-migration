package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "APPLIANCE_INFO")
@Data
public class ApplianceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "APPLIANCE_NAME")
    private String applianceName;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
}
