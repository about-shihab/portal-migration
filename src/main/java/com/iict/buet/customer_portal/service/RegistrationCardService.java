package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.dto.Response;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RegistrationCardService {
    ResponseEntity<?> getRegistrationCard();


    ResponseEntity<?> prepareRegistrationCard();

    ResponseEntity<Response> createDuplicateCardIssueRequest(HttpServletRequest request, HttpServletResponse response);
}
