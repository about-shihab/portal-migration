package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "NM_CUST_APPLIANCE")
@Data
public class NonMeteredCustomerAppliance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "CUSTOMER_ID")
    private Long customerId;
    private Long quantity;
    @Column(name = "APPLIANCE_INFO_ID")
    private Long applianceInfoId;

}
