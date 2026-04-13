package com.iict.buet.customer_portal.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ForgotPasswordRequestDto {
    @NotBlank(message = "username can't be blank")
    private String username;
}
