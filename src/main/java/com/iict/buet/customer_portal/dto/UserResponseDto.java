package com.iict.buet.customer_portal.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseDto {

    private Long id;
    private String email;
    private String password;
    private String status;
    private String userName;
    private String country;
    private String contactNumber;
    private String address;
    private String registrationNumber;
    private String userType;
    private int noOfAttempt;

    private List<RoleDto> roles;
}
