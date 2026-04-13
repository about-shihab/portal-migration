package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.dto.*;
import com.iict.buet.customer_portal.model.InitialChangeMobileNumberDto;
import com.iict.buet.customer_portal.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    User getUserByEmailOrUserName(String emailOrUserName);

    Response validateCustomers(UserDto userDto, HttpServletRequest request, HttpServletResponse response);

    Response create(UserDto userDto);

    Response verifyOtp(String otp, HttpServletRequest request, HttpServletResponse response);

    Response activateUser(ActivateUserDto activateUserDto);

    String activateUser(UserDto userDto, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request);


    void deleteAllExpiredToken();

    void updateAllUserPassword();

    Response createForgotPasswordRequest(ForgotPasswordRequestDto forgotPasswordRequestDto, HttpServletRequest request, HttpServletResponse response);

    Response changeForgottenPassword(ForgotPasswordDto forgotPasswordDto);

    Response changeForgottenPassword(ForgotPasswordDto forgotPasswordDto, BindingResult bindingResult, HttpServletResponse response, HttpServletRequest request);

    Response resetPassword(ResetPasswordDto resetPasswordDto);

    Response testPassword(String password);

    Response matchPassword(String password);

    ResponseEntity<Response> getAllMobileNumberLogByCustomerCode();


    ResponseEntity<Response> processOtpRequest(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<Response> createChangeMobileNumberRequest(InitialChangeMobileNumberDto changeMobileNumberDto, HttpServletRequest request, HttpServletResponse response);

    User getUserAndValidate();


    boolean isActiveOTPExist(String otp);

    boolean verifyAndExpireOtp(String otp);

    ResponseEntity<Response> changeMobileNumber(ChangeMobileNumberDto changeMobileNumberDto);
}
