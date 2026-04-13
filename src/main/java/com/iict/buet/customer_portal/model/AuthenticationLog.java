package com.iict.buet.customer_portal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "CUSTOMER_PORTAL_AUTH_LOG")
@Getter
@Setter
public class AuthenticationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String username;

    @Column(name = "LOGIN_TIME")
    private LocalDateTime loginTime;

    @Column(name = "LOGOUT_TIME")
    private LocalDateTime logoutTime;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @Column(name = "SESSION_ID")
    private String sessionId;

    // whether the login was successful or not
    private boolean success = true;

    // stores the username of the user who is impersonating the customer
    // null indicates that the user is login in as himself
    private String impersonator;

    // if the login was unsuccessful, this field stores the reason
    private String remarks;

}