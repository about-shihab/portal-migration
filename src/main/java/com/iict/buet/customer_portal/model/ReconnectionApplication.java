package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "RECONNECTION_APPLICATION")
public class ReconnectionApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "cust_code", nullable = false, length = 100)
    private String customerCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "application_date", nullable = false)
    private Date applicationDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "disconnection_date")
    private Date disconnectionDate;

    @Column(name = "zone", nullable = false, length = 30)
    private String zone;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private Status status;

    @Column(name = "approved_by")
    private String approvedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "approved_date")
    private Date approvedDate;

    @OneToOne
    @JoinColumn(name = "attachement_file_info_id")
    private FileInfo attachmentFileInfo;

    @OneToOne
    @JoinColumn(name = "app_file_info_id")
    private FileInfo appFileInfo;

    public enum Status {
        PENDING, APPROVED
    }
}
