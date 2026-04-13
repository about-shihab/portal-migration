package com.iict.buet.customer_portal.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "CUSTOMER_PORTAL_USER")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ParamGenerator")
    @SequenceGenerator(name = "ParamGenerator", sequenceName = "CUSTOMER_PORTAL_USER_SEQ", allocationSize = 1)
    private Long id;
    @NaturalId
    @Column(name = "CUST_CODE")
    private String customerCode;
    private String password;
    @Temporal(TemporalType.DATE)
    @Column(name = "last_otp_send_date")
    private Date lastOtpSendDate;
    @Column(name = "no_of_send_otp")
    private Long noOfSendOtp;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CUSTOMER_PORTAL_USER_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    private List<Role> roles;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "created_by")
    private Long createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "updated_by")
    private Long updatedBy;
    @Column(name = "is_active")
    private Boolean isActive;

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
        this.lastOtpSendDate = new Date();
        this.isActive = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}