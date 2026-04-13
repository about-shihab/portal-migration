package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.dto.AdminLoginRequest;
import com.iict.buet.customer_portal.dto.LoginRequestDto;
import com.iict.buet.customer_portal.dto.Response;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {
    Response login(LoginRequestDto loginRequestDto, HttpServletRequest request, boolean isAdmin, AdminLoginRequest adminLoginRequestDto);

    Response logout(LoginRequestDto loginRequestDto);
}
