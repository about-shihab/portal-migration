package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.dto.*;
import com.iict.buet.customer_portal.jwtconfig.JwtTokenProvider;
import com.iict.buet.customer_portal.model.AdminUser;
import com.iict.buet.customer_portal.repository.AdminUserRepository;
import com.iict.buet.customer_portal.repository.CustomerDomesticRepository;
import com.iict.buet.customer_portal.repository.CustomerRepository;
import com.iict.buet.customer_portal.service.AuthService;
import com.iict.buet.customer_portal.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service("authService")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final CustomerDomesticRepository customerDomesticRepository;
    private final CustomerRepository customerRepository;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Response login(LoginRequestDto loginRequestDto, HttpServletRequest request, boolean isAdmin, AdminLoginRequest adminLoginRequestDto) {
        try {
            UsernamePasswordAuthenticationToken token;
            Authentication authentication;
            if (!isAdmin) {
                token = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
            } else {
                Optional<AdminUser> adminUser = adminUserRepository.findByUserIdOrUserNameAndIsActive(
                        adminLoginRequestDto.getUsername(),
                        adminLoginRequestDto.getUsername(),
                        true
                );
                if (!adminUser.isPresent() || !passwordEncoder.matches(adminLoginRequestDto.getPassword(), adminUser.get().getPassword())) {
                    return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Password or username is incorrect. Try again.");
                }
                // Explicitly add ADMIN role for checking in AdminAuthenticationProvider
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ADMIN"));
                token = new UsernamePasswordAuthenticationToken(adminLoginRequestDto.getCustomerCode(), null, authorities);
            }

            authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (authentication.isAuthenticated()) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                String customerCode = userPrincipal.getCustomerCode();
                Boolean isExist = false;
                if (customerCode != null) {
                    int countCustomer = 0;
                    if (customerCode.contains("NM")) {
                        countCustomer = customerDomesticRepository.countByCodeAndIsRegisteredTrue(customerCode);
                    } else {
                        countCustomer = customerRepository.countByCodeAndIsRegisteredTrue(customerCode);
                    }
                    if (countCustomer == 1) {
                        isExist = true;
                    }
                }
                if (isExist) {
                    LoginResponseDto loginResponseDto = buildUserResponseDto(authentication, request);
                    return ResponseBuilder.getSuccessResponse(HttpStatus.OK, loginResponseDto, "Logged In Successfully.");
                }
                return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Password or username is incorrect or not online registered. Try again.");
            }
            return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Password or username is incorrect. Try again.");
        } catch (Exception e) {
            return ResponseBuilder.getFailResponse(HttpStatus.UNAUTHORIZED, "Password or username is incorrect. Try again.");
        }
    }

    private LoginResponseDto buildUserResponseDto(Authentication authentication, HttpServletRequest request) {
        String token = tokenProvider.generateToken(authentication, request);
        List<RoleDto> roleDtoList = new ArrayList<>();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        principal.getAuthorities().forEach(grantedAuthority -> {
            RoleDto dto = new RoleDto();
            dto.setName(grantedAuthority.getAuthority());
            roleDtoList.add(dto);
        });
        return new LoginResponseDto(token, principal.getCustomerCode(), principal.getCustomerCode(), principal.getId(), roleDtoList);
    }


    @Override
    public Response logout(LoginRequestDto loginRequestDto) {

        return null;
    }
}
