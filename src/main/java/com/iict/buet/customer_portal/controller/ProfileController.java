package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.annotations.ApiController;
import com.iict.buet.customer_portal.annotations.DataValidation;
import com.iict.buet.customer_portal.annotations.DataValidation2;
import com.iict.buet.customer_portal.dto.ChangeMobileNumberDto;
import com.iict.buet.customer_portal.dto.ResetPasswordDto;
import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.model.InitialChangeMobileNumberDto;
import com.iict.buet.customer_portal.service.ProfileService;
import com.iict.buet.customer_portal.service.UserService;
import com.iict.buet.customer_portal.util.UrlConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstants.UserManagement.ROOT)
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping(UrlConstants.UserManagement.GET_INFO)
    public Response getInfo(HttpServletRequest request, HttpServletResponse response) {
        return profileService.getProfileInfo();
    }

    @PutMapping(UrlConstants.UserManagement.CHANGE_PASSWORD)
    @DataValidation
    public Response changePassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return userService.resetPassword(resetPasswordDto);
    }

    @GetMapping(UrlConstants.UserManagement.CHANGE_MOBILE_NUMBER_LOG)
    public ResponseEntity<Response> getAllMobileNumberLogByCustomerCode(HttpServletRequest request, HttpServletResponse response) {
        return userService.getAllMobileNumberLogByCustomerCode();
    }

    @PutMapping(UrlConstants.UserManagement.CHANGE_MOBILE_NUMBER_OTP_REQUEST)
    @DataValidation2
    public ResponseEntity<Response> changeMobileNumberOtpRequest(@RequestBody @Valid InitialChangeMobileNumberDto changeMobileNumberDto,
                                                                 BindingResult bindingResult,
                                                                 HttpServletRequest request,
                                                                 HttpServletResponse response) {
        return userService.createChangeMobileNumberRequest(changeMobileNumberDto, request, response);
    }

    @PutMapping(UrlConstants.UserManagement.CHANGE_MOBILE_NUMBER)
    @DataValidation2
    public ResponseEntity<Response> changeMobileNumber(@RequestBody @Valid ChangeMobileNumberDto changeMobileNumberDto,
                                                       BindingResult bindingResult,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        return userService.changeMobileNumber(changeMobileNumberDto);
    }

    @GetMapping(UrlConstants.UserManagement.MATCH_PASSWORD)
    public Response matchPassword(@RequestParam String password, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return userService.matchPassword(password);
    }
}
