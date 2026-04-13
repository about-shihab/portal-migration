package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "GATEWAY_RESPONSE")
@Data
public class GatewayResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ParamGenerator")
    @SequenceGenerator(name = "ParamGenerator", sequenceName = "GATEWAY_RESPONSE_seq", allocationSize = 1)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTION_DATE", updatable = false)
    private Date actionDate;
    @Column(name = "IS_USED")
    private Boolean isUsed;
    @Column(name = "IS_SUCCESS")
    private Boolean isSuccess;
    @Column(name = "CUSTOMER_IP")
    private String customerIp;
    private String response;
    private String request;
    @Column(name = "CUSTOMER_CODE")
    private String customerCode;
    @Column(name = "GATEWAY_NAME")
    private String gatewayName;
    private String rrn;
    @Column(name = "KGDCL_TRA_REF")
    private String kgdclTraRef;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @PrePersist
    public void prePersist() {
        this.actionDate = new Date();
    }
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

}
