package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.annotations.DataValidation;
import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.dto.UserDto;
import com.iict.buet.customer_portal.service.UserService;
import com.iict.buet.customer_portal.util.UrlConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Size;

@Controller
@RequiredArgsConstructor
@RequestMapping(UrlConstants.AuthManagement.SIGNUP)
public class RegistrationController {
    private final UserService userService;

    @PostMapping(value = UrlConstants.AuthManagement.VALIDATE_CUSTOMER)
    @DataValidation
    @ResponseBody
    public Response checkCustomer(@Validated({UserDto.CustomerValidation.class}) @RequestBody @Valid UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.validateCustomers(userDto, request, response);
    }


    @PostMapping(value = UrlConstants.AuthManagement.OTP_VERIFY)
    @DataValidation
    @ResponseBody
    public Response verifyOtp(@RequestParam @Size(max = 10) String otp, HttpServletResponse servletResponse, HttpServletRequest request) {
        return userService.verifyOtp(otp, request, servletResponse);
    }


    @PostMapping(value = UrlConstants.AuthManagement.ACTIVATE_USER)
    public String activateUser(@ModelAttribute @Validated(UserDto.PasswordValidation.class) UserDto userDto, BindingResult bindingResult,
                               HttpServletResponse response, HttpServletRequest request) {
        return userService.activateUser(userDto, bindingResult, response, request);
    }

}