package com.iict.buet.customer_portal.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "DUPLICATE_CARD_ISSUE_LOG")
@Getter
@Setter
@RequiredArgsConstructor
public class DuplicateCardIssueLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "CUST_CODE", nullable = false)
    private String customerCode;

    @Column(name = "CARD_ISSUE_DATE_TIME", nullable = false)
    private LocalDateTime cardIssueDateTime;

    @Column(name = "ISSUED_BY", nullable = false)
    private String issuedBy;

    @Column(name = "DUPLICATE_REASON")
    private String duplicateReason;

    @Column(name = "REMARKS")
    private String remarks;

}
