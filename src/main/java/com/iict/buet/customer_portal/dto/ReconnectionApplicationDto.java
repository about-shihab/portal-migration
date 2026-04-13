package com.iict.buet.customer_portal.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReconnectionApplicationDto {
    ProfileDto profile;
    private Date disconnectionDate;
    private String disconnectionCauses;
    private boolean dueBillExist;
    private boolean isDisconnected;
}
