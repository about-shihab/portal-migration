package com.iict.buet.customer_portal.dto;

import lombok.Data;

@Data
public class VerificationOtpDto {
    private Long id;
    private String email;
    private String otp;
    private String temporaryToken;
}
