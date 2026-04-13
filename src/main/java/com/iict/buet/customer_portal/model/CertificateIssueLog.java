package com.iict.buet.customer_portal.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "CERTIFICATE_ISSUE_LOG")
@Getter
@Setter
@RequiredArgsConstructor
public class CertificateIssueLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "CUST_CODE", nullable = false)
    private String customerCode;

    @Column(name = "ISSUE_DATE_TIME", nullable = false)
    private LocalDateTime issueDateTime;

    @Column(name = "ISSUED_BY", nullable = false)
    private String issuedBy;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "REMARKS")
    private String remarks;
}
