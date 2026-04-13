package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.config.EnvironmentProperties;
import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.model.DuplicateCardIssueLog;
import com.iict.buet.customer_portal.model.User;
import com.iict.buet.customer_portal.repository.DuplicateCardIssueLogRepository;
import com.iict.buet.customer_portal.service.RegistrationCardService;
import com.iict.buet.customer_portal.service.UserService;
import com.iict.buet.customer_portal.service.UtilService;
import com.iict.buet.customer_portal.util.AuthUtils;
import com.iict.buet.customer_portal.util.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
public class RegistrationCardServiceImpl implements RegistrationCardService {
    private static final Logger logger = LogManager.getLogger(RegistrationCardServiceImpl.class.getName());
    private final UtilService utilService;
    private final DuplicateCardIssueLogRepository duplicateCardIssueLogRepository;
    private final AuthUtils authUtils;
    private final UserService userService;

    private final EnvironmentProperties envProps;

    public RegistrationCardServiceImpl(UtilService utilService, DuplicateCardIssueLogRepository duplicateCardIssueLogRepository, AuthUtils authUtils, UserService userService, EnvironmentProperties envProps) {
        this.utilService = utilService;
        this.duplicateCardIssueLogRepository = duplicateCardIssueLogRepository;
        this.authUtils = authUtils;
        this.userService = userService;
        this.envProps = envProps;
    }

    @Override
    public ResponseEntity<?> getRegistrationCard() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        // TODO: Check how to provide env variable to avoid trim()
        String url = envProps.getERP_SERVER_URL().trim() + "/customerInternal/regCardPrint";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", envProps.getERP_SERVER_BASIC_AUTH());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("customerCode", customerCode)
                .queryParam("duplicate", 1)
                .queryParam("reRegistration", 0);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                byte[].class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            byte[] pdfContent = responseEntity.getBody();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_PDF);
            responseHeaders.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("registration-card-" + customerCode + ".pdf").build());

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(pdfContent);
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).body("Error occurred while fetching report");
        }
    }

    @Override
    public ResponseEntity<?> prepareRegistrationCard() {
        String customerCode = utilService.getLoggedInUserCustomerCode();

        Resource reportResource = new ClassPathResource("reports/duplicate_reg_card.jasper");

        Map<String, Object> reportParams = new HashMap<>();
        reportParams.put("IMAGE_DIR", "classpath:/static/images");
        reportParams.put("duplicate", "1");
        reportParams.put("cust_code", customerCode);
        try (InputStream reportStream = reportResource.getInputStream()) {
            ResponseEntity<?> response = utilService.prepareReport(reportParams, reportStream, customerCode, "registration-card", "Registration Card");

            if (response.getStatusCode() == HttpStatus.OK) {
                DuplicateCardIssueLog log = new DuplicateCardIssueLog();
                log.setCustomerCode(customerCode);
                log.setCardIssueDateTime(LocalDateTime.now());
                log.setIssuedBy(utilService.getLoggedInUserCustomerCode());
                log.setDuplicateReason("Customer Initialized");
                log.setRemarks("Duplicate Registration card generated from portal");
                duplicateCardIssueLogRepository.save(log);
            }

            return response;
        } catch (IOException ex) {
            logger.error("Failed to load report resource", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while preparing the registration card.");
        }
    }

    @Override
    public ResponseEntity<Response> createDuplicateCardIssueRequest(HttpServletRequest request, HttpServletResponse response) {
        if (authUtils.canUserImpersonateCustomer()) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.UNAUTHORIZED, "Admin users are not allowed to issue duplicate cards");
        }

        User user = userService.getUserAndValidate();
        if (user == null) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.BAD_REQUEST, "Requested user not found");
        }
        return userService.processOtpRequest(request, response);
    }
}
