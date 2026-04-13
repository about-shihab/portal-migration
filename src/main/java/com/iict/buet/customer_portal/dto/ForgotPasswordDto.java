package com.iict.buet.customer_portal.dto;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.iict.buet.customer_portal.util.SystemConstants.PASSWORD_REGEX;

@Data
public class ForgotPasswordDto {
    public interface TokenValidation {
    }
    public interface PasswordValidation {

    }
    @NotBlank(groups = {TokenValidation.class},message = "Token can't be empty")
    private String token;
    @NotBlank(message = "Username can't be empty")
    private String username;
    @Size(groups = {PasswordValidation.class}, min = 8, max = 100, message = "New Password Must be within 8 to 100 characters")
    @Pattern(groups = {PasswordValidation.class}, regexp = PASSWORD_REGEX, message = "Password must contain at least one letter, one digit, and be at least 8 characters long")
    private String newPassword;
    @Size(groups = {PasswordValidation.class},min = 8, max = 100, message = "Confirm password Must be within 8 to 100 characters")
    @Pattern(groups = {PasswordValidation.class},regexp = PASSWORD_REGEX, message = "Password must contain at least one letter, one digit, and be at least 8 characters long")
    private String confirmPassword;
}
