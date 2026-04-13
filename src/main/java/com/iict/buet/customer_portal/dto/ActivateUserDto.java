package com.iict.buet.customer_portal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ActivateUserDto {
    @NotBlank(message = "Customer Code required")
    private String customerCode;
    @NotBlank(message = "Token required")
    private String token;
}
