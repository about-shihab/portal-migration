package com.iict.buet.customer_portal.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProfileDto {
    private String name;
    private String customerCode;
    private String oldCode;
    private String mobileNo;
    private String email;
    private Boolean isNonMetered;
    private String username;
    private String customerType;
    private String connectionId;
    private String connectionStatus;
    private String zone;
    private String address;
    private List<ApplianceInfoDto> applianceInfoList;
    private List<MeterInfoDto> meterInfoList;
}
