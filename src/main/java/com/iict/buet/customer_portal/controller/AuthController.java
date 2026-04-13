package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.annotations.ApiController;
import com.iict.buet.customer_portal.annotations.DataValidation;
import com.iict.buet.customer_portal.dto.*;
import com.iict.buet.customer_portal.service.AuthService;
import com.iict.buet.customer_portal.service.BillInfoService;
import com.iict.buet.customer_portal.service.UserService;
import com.iict.buet.customer_portal.util.UrlConstants;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstants.AuthManagement.ROOT)
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final BillInfoService billInfoService;

    public AuthController(AuthService authService, UserService userService, BillInfoService billInfoService) {
        this.authService = authService;
        this.userService = userService;
        this.billInfoService = billInfoService;
    }

    @PostMapping(UrlConstants.AuthManagement.LOGIN)
    public Response login(@Validated({LoginRequestDto.CreateValidation.class}) @RequestBody LoginRequestDto loginRequestDto, BindingResult bindingResults, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = authService.login(loginRequestDto, request, false, null);
        httpServletResponse.setStatus(response.getStatusCode());
        return response;
    }

    @PostMapping(UrlConstants.AuthManagement.ADMIN_LOGIN)
    public Response adminLogin(@Validated @RequestBody AdminLoginRequest adminLoginRequest, BindingResult bindingResults, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = authService.login(null, request, true, adminLoginRequest);
        httpServletResponse.setStatus(response.getStatusCode());
        return response;
    }

    @GetMapping(UrlConstants.AuthManagement.BANK_LIST)
    public Response getBankList(HttpServletRequest request, HttpServletResponse response) {
        return billInfoService.getBankList();
    }

    @PostMapping(UrlConstants.AuthManagement.CREATE)
    @DataValidation
    public Response create(@RequestBody @Valid UserDto userDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return userService.create(userDto);
    }

    @GetMapping(UrlConstants.AuthManagement.TEST_PASSWORD)
    public Response testPassword(@RequestParam("password") String password, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return userService.testPassword(password);
    }

    @PostMapping(UrlConstants.AuthManagement.FORGOT_PASSWORD)
    @DataValidation
    public Response forgotPassword(@RequestBody @Valid ForgotPasswordRequestDto forgotPasswordRequestDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return userService.createForgotPasswordRequest(forgotPasswordRequestDto,request,httpServletResponse);
    }

    @PutMapping(UrlConstants.AuthManagement.SET_NEW_PASSWORD_FOR_FORGOT)
    @DataValidation
    public Response setNewPasswordForForgot(@RequestBody @Validated({ForgotPasswordDto.PasswordValidation.class}) ForgotPasswordDto forgotPasswordDto, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request) {
        return userService.changeForgottenPassword(forgotPasswordDto,bindingResult,response,request);
    }

    @PutMapping(UrlConstants.AuthManagement.ACTIVATE_USER)
    @DataValidation
    public Response activateUser(@RequestBody @Valid ActivateUserDto activateUserDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return userService.activateUser(activateUserDto);
    }

}
