package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.dto.ComplainInfoDto;
import com.iict.buet.customer_portal.dto.Response;

public interface ComplainService {
    Response create(ComplainInfoDto complainInfoDto);
    Response createReview(String ticketNo, String feedback);
    Response getComplainCauseList();
    Response getReviewStatusList();
    Response getTicketStatusList();
}
