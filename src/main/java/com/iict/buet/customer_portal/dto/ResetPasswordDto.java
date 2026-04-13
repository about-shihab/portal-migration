package com.iict.buet.customer_portal.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.iict.buet.customer_portal.util.SystemConstants.PASSWORD_REGEX;

@Data
public class ResetPasswordDto {
    @NotEmpty(message = "Current password can not be empty")
    private String password;
    @Size(min = 8, max = 100, message = "New Password Must be within 8 to 100 characters")
    @Pattern(regexp = PASSWORD_REGEX, message = "Password must contain at least one letter, one digit, and be at least 8 characters long")
    private String newPassword;
    @Pattern(regexp = PASSWORD_REGEX, message = "Password must contain at least one letter, one digit, and be at least 8 characters long")
    private String confirmPassword;
}
