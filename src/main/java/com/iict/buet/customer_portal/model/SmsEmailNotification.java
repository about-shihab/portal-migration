package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "SMS_EMAIL_NOTIFICATION")
@Data
public class SmsEmailNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ParamGenerator")
    @SequenceGenerator(name = "ParamGenerator", sequenceName = "sms_email_notification_seq", allocationSize = 1)
    private Long id;
    private String email;
    private String mobile;
    private String type;
    private String message;
    private String status;
    private String subject;

}
