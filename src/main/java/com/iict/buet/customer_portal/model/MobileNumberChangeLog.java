package com.iict.buet.customer_portal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "MOBILE_NUMBER_CHANGE_LOG")
@Getter
@Setter
public class MobileNumberChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "CUST_CODE", nullable = false)
    private String customerCode;

    @Column(name = "OLD_MOBILE_NO")
    private String oldMobileNo;

    @Column(name = "NEW_MOBILE_NO")
    private String newMobileNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CHANGED_DATE_TIME")
    private LocalDateTime changedDateTime;

    @Column(name = "CHANGED_BY")
    private String changedBy;

    @Column(name = "REMARKS")
    private String remarks;
}