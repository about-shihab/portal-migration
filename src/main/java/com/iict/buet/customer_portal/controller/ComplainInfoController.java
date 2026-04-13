package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.annotations.ApiController;
import com.iict.buet.customer_portal.annotations.DataValidation;
import com.iict.buet.customer_portal.dto.ComplainInfoDto;
import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.service.ComplainService;
import com.iict.buet.customer_portal.util.UrlConstants;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstants.ComplainInfoManagement.ROOT)
public class ComplainInfoController {
    private final ComplainService complainService;

    public ComplainInfoController(ComplainService complainService) {
        this.complainService = complainService;
    }

    @PostMapping(UrlConstants.ComplainInfoManagement.CREATE)
    @DataValidation
    public Response createComplain(@RequestBody @Valid ComplainInfoDto complainInfoDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return complainService.create(complainInfoDto);
    }

    @PostMapping(UrlConstants.ComplainInfoManagement.REVIEW)
    public Response createComplainReview(@RequestParam("ticketNo") String ticketNo, @RequestParam("feedback") String feedback, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return complainService.createReview(ticketNo, feedback);
    }

    @GetMapping(UrlConstants.ComplainInfoManagement.COMPLAIN_CAUSE_LIST)
    public Response getComplainCauseList(HttpServletRequest request, HttpServletResponse response) {
        return complainService.getComplainCauseList();
    }

    @GetMapping(UrlConstants.ComplainInfoManagement.COMPLAIN_TICKET_STATUS_LIST)
    public Response getComplainTicketStatusList(HttpServletRequest request, HttpServletResponse response) {
        return complainService.getTicketStatusList();
    }

    @GetMapping(UrlConstants.ComplainInfoManagement.COMPLAIN_REVIEW_STATUS_LIST)
    public Response getComplainReviewList(HttpServletRequest request, HttpServletResponse response) {
        return complainService.getReviewStatusList();
    }
}
