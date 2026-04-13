package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "INSTALLED_METER_INFO")
public class InstalledMeterInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "CUST_CODE")
    private String customerCode;
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
    @Column(name = "METER_NO")
    private String meterNo;
    @Column(name = "METER_TYPE")
    private String meterType;
    @Column(name = "METER_STATUS")
    private String meterStatus;
    @Column(name = "METER_GROUP")
    private String meterGroup;
    @Column(name = "METER_CODE")
    private String meterCode;

}
