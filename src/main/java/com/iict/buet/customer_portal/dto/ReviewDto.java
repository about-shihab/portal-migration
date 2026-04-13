package com.iict.buet.customer_portal.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private String reviewDate;
    private String reviewerFeedback;
    private Long complainId;
    private String kgdclFeedback;
}
