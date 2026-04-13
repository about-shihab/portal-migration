package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@MappedSuperclass
@Data
public class BaseAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "HOLDING_NO")
    private String holdingNo;
    private String title;
    private String moholla;
    @Column(name = "ROAD_NO")
    private String roadNo;
    @Column(name = "POLICE_STATION")
    private String policeStation;
    private String district;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "CUST_ID")
    private Long customerId;
}
