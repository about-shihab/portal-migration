package com.iict.buet.customer_portal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iict.buet.customer_portal.config.EnvironmentProperties;
import com.iict.buet.customer_portal.dto.*;
import com.iict.buet.customer_portal.service.ComplainService;
import com.iict.buet.customer_portal.service.UtilService;
import com.iict.buet.customer_portal.util.AuthUtils;
import com.iict.buet.customer_portal.util.HttpUrlConnectionUtil;
import com.iict.buet.customer_portal.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service("complainService")
@RequiredArgsConstructor
public class ComplainServiceImpl implements ComplainService {
    private final ObjectMapper objectMapper;
    private final UtilService utilService;
    private final AuthUtils authUtils;
    private final EnvironmentProperties envProps;

    @Override
    public Response create(ComplainInfoDto complainInfoDto) {
        Response res = authUtils.denyAdminAccess("Admin users are not allowed to create complaint.");
        if (res != null) {
            return res;
        }
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String url = envProps.getERP_SERVER_URL().trim() + "/complainInfo/savePortal/" + complainInfoDto.getCauseId() + "/" + customerCode + "/" + HttpUrlConnectionUtil.encodeValue(complainInfoDto.getDescription());
        String responseString = HttpUrlConnectionUtil.getContent(url, "POST", envProps.getERP_SERVER_BASIC_AUTH());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            ComplainInfoResponse response = objectMapper.readValue(responseString, ComplainInfoResponse.class);
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, response, "Customer Complain");
        } catch (JsonProcessingException e) {
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs");
        }
    }

    @Override
    public Response createReview(String ticketNo, String feedback) {
        Response res = authUtils.denyAdminAccess("Admin users are not allowed to submit complaint review.");
        if (res != null) {
            return res;
        }
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String url = envProps.getERP_SERVER_URL().trim() + "/complainInfo/saveReviewPortal/" + ticketNo + "/" + customerCode + "/" + HttpUrlConnectionUtil.encodeValue(feedback);
        String responseString = HttpUrlConnectionUtil.getContent(url, "POST", envProps.getERP_SERVER_BASIC_AUTH());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            ComplainInfoResponse response = objectMapper.readValue(responseString, ComplainInfoResponse.class);
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, response, "Customer Complain Review");
        } catch (JsonProcessingException e) {
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs");
        }
    }

    @Override
    public Response getComplainCauseList() {
        String url = envProps.getERP_SERVER_URL().trim() + "/complainInfo/complainCausePortal";
        String responseString = HttpUrlConnectionUtil.getContent(url, "GET", envProps.getERP_SERVER_BASIC_AUTH());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            ComplainCauseDto[] response = objectMapper.readValue(responseString, ComplainCauseDto[].class);
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, response, "Customer Complain Cause List");
        } catch (JsonProcessingException e) {
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs");
        }
    }

    @Override
    public Response getReviewStatusList() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String url = envProps.getERP_SERVER_URL().trim() + "/complainInfo/portalReviewStatusList?customerCode=" + customerCode;
        String responseString = HttpUrlConnectionUtil.getContent(url, "GET", envProps.getERP_SERVER_BASIC_AUTH());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            ReviewDto[] response = objectMapper.readValue(responseString, ReviewDto[].class);
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, response, "Customer Complain Review List");
        } catch (JsonProcessingException e) {
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs");
        }
    }

    @Override
    public Response getTicketStatusList() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String url = envProps.getERP_SERVER_URL().trim() + "/complainInfo/portalTokenStatusList?customerCode=" + customerCode;
        String responseString = HttpUrlConnectionUtil.getContent(url, "GET", envProps.getERP_SERVER_BASIC_AUTH());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            TicketDto[] response = objectMapper.readValue(responseString, TicketDto[].class);
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, response, "Customer Complain ticket List");
        } catch (JsonProcessingException e) {
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs");
        }
    }
}
