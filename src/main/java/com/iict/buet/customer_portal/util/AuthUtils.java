package com.iict.buet.customer_portal.util;

import com.iict.buet.customer_portal.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.stereotype.Component;

import static com.iict.buet.customer_portal.util.SystemConstants.ROLE_ADMIN;
import static org.springframework.security.web.authentication.switchuser.SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR;

@Component
public class AuthUtils {
    public String getLoggedInUser() {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (current == null) return null;

        for (GrantedAuthority authority : current.getAuthorities()) {
            if (authority instanceof SwitchUserGrantedAuthority) {
                return ((SwitchUserGrantedAuthority) authority).getSource().getName() + " | " + current.getName();
            }
        }
        return current.getName();
    }

    public String getImpersonatedUser() {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (current == null) return null;

        return current.getAuthorities().stream()
                .filter(authority -> authority instanceof SwitchUserGrantedAuthority)
                .findFirst()
                .map(authority -> ((SwitchUserGrantedAuthority) authority).getSource().getName())
                .orElse("");
    }

    public boolean canUserImpersonateCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) return false;

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> ROLE_ADMIN.equals(authority.getAuthority()) ||
                        ROLE_PREVIOUS_ADMINISTRATOR.equals(authority.getAuthority()));
    }

    public boolean hasOnlyAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) return false;

        return authentication.getAuthorities().size() == 1 &&
                authentication.getAuthorities().stream()
                        .anyMatch(authority -> ROLE_ADMIN.equals(authority.getAuthority()));
    }

    public Response denyAdminAccess(String message) {
        if (canUserImpersonateCustomer())
            return ResponseBuilder.getFailResponse(HttpStatus.FORBIDDEN,
                    message == null ? "Admin users are not allowed to perform this operation" : message);
        return null;
    }
}
