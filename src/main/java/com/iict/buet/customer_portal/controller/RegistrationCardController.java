package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.service.RegistrationCardService;
import com.iict.buet.customer_portal.service.UserService;
import com.iict.buet.customer_portal.util.UrlConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(UrlConstants.RegistrationCardManagement.ROOT)
@RequiredArgsConstructor
public class RegistrationCardController {
    private final RegistrationCardService registrationCardService;
    private final UserService userService;

    @PostMapping(UrlConstants.RegistrationCardManagement.VALIDATE_OTP)
    public ResponseEntity<?> validateOtp(@RequestBody String otp) {
        if (!userService.isActiveOTPExist(otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid OTP. Please enter a valid OTP to download the card.");
        } else {
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping(UrlConstants.RegistrationCardManagement.DOWNLOAD)
    public ResponseEntity<?> downloadRegistrationCard(@RequestParam("otp") String otp) {
        if (!userService.verifyAndExpireOtp(otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid OTP. Please enter a valid OTP to download the card.");
        }
        return registrationCardService.prepareRegistrationCard();
    }

    @PostMapping(UrlConstants.RegistrationCardManagement.ISSUE_REQUEST)
    public ResponseEntity<Response> IssueRegCardAndSendOtp(HttpServletRequest request, HttpServletResponse response) {
        return registrationCardService.createDuplicateCardIssueRequest(request, response);
    }
}
