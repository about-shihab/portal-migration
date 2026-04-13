package com.iict.buet.customer_portal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iict.buet.customer_portal.util.SystemConstants;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {
    public interface CustomerValidation {

    }

    public interface PasswordValidation {

    }

    private Long id;
    @NotBlank(groups = {CustomerValidation.class}, message = "Customer code can't be blank")
    private String customerCode;
    @NotBlank(groups = {CustomerValidation.class}, message = "Phone no. can't be blank")
    @Size(groups = {CustomerValidation.class}, min = 11, max = 11, message = "Phone no. must be 11 characters")
    @Pattern(groups = {CustomerValidation.class}, regexp = "^[0][1]\\d{9}$", message = "Phone no. must starts with 01")
    private String phoneNo;
    @NotBlank(groups = {PasswordValidation.class}, message = "Password can't be blank")
    @Size(groups = {PasswordValidation.class}, min = 8, max = 100, message = "Password Must be within 8 to 100 characters")
    @Pattern(groups = {PasswordValidation.class}, regexp = SystemConstants.PASSWORD_REGEX, message = "Password must contain at least one letter, one digit, and be at least 8 characters long")
    private String password;

    private List<RoleDto> roles;
}
