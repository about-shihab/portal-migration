package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "DISCONNECTION_DETAIL")
public class DisconnectionDetail {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "CUST_CODE")
    private String custCode;

    @Column(name = "DISCONNECTION_DATE_TIME")
    private Date disconnectionDateTime;

    @Column(name = "REFERENCE_NO")
    private String referenceNo;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DISCONNECTION_TYPE")
    private String disconnectionType;

    @Column(name = "APPLICATION_DATE")
    private Date applicationDate;

    @Column(name = "CAUSES")
    private String causes;
}
