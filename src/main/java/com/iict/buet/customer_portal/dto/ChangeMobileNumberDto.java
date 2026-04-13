package com.iict.buet.customer_portal.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.iict.buet.customer_portal.util.SystemConstants.MOBILE_NUMBER_REGEX;

@Data
public class ChangeMobileNumberDto {
    @NotEmpty(message = "Current mobile can not be empty")
    @Pattern(regexp = MOBILE_NUMBER_REGEX, message = "Provided current mobile number is not valid")
    private String mobileNumber;
    @NotEmpty(message = "New mobile can not be empty")
    @Pattern(regexp = MOBILE_NUMBER_REGEX, message = "Provided new mobile number is not valid")
    private String newMobileNumber;
    @NotEmpty(message = "OTP can not be empty")
    private String otp;
}
