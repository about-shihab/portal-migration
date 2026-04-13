package com.iict.buet.customer_portal.util;

import com.iict.buet.customer_portal.dto.UserPrincipal;
import com.iict.buet.customer_portal.model.AuthenticationLog;
import com.iict.buet.customer_portal.repository.AuthenticationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.switchuser.AuthenticationSwitchUserEvent;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;

import static com.iict.buet.customer_portal.util.SystemConstants.ROLE_ADMIN;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {
    private final AuthenticationLogRepository authenticationLogRepository;

    @EventListener
    @Async
    public void onSuccessEvent(AuthenticationSuccessEvent event) {
        AuthenticationLog authenticationLog = createAuthenticationLog(event);
        authenticationLogRepository.save(authenticationLog);
    }

    @EventListener
    @Async
    public void onLogoutSuccessEvent(LogoutSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        authenticationLogRepository.updateLogout(Instant.ofEpochMilli(event.getTimestamp())
                .atZone(ZoneId.systemDefault()).toLocalDateTime(), details.getSessionId());
    }

    @EventListener
    @Async
    public void onFailureEvent(AbstractAuthenticationFailureEvent event) {
        AuthenticationLog authenticationLog = createAuthenticationLog(event);
        authenticationLog.setRemarks(event.getException().getMessage());
        authenticationLog.setSuccess(false);
        authenticationLogRepository.save(authenticationLog);
    }

    @EventListener
    @Async
    public void onSwitchUserEvent(AuthenticationSwitchUserEvent event) {
        if (hasAdminRole(event.getTargetUser().getAuthorities())) {
            // switch from customer to admin
            // although this is not possible in our system, but spring security does publish this event
            // when admin is already switched to a customer and then tries to switch to another customer
            return;
        }

        Authentication authentication = event.getAuthentication();
        AuthenticationLog authenticationLog = createAuthenticationLog(event);

        if (event.getTargetUser() instanceof UserPrincipal && hasAdminRole(authentication.getAuthorities())) {
            // first time switch from admin to customer after login
            authenticationLog.setImpersonator(authentication.getName());
            authenticationLog.setUsername(event.getTargetUser().getUsername());
        } else {
            // switch from one already switched customer to another by admin
            authenticationLog.setUsername(event.getTargetUser().getUsername());
            authenticationLog.setImpersonator(getSourceUserName(authentication));
        }
        authenticationLogRepository.save(authenticationLog);

    }

    private AuthenticationLog createAuthenticationLog(AbstractAuthenticationEvent event) {
        AuthenticationLog authenticationLog = new AuthenticationLog();
        Authentication authentication = event.getAuthentication();
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        authenticationLog.setUsername(authentication.getName());
        authenticationLog.setLoginTime(Instant.ofEpochMilli(event.getTimestamp())
                .atZone(ZoneId.systemDefault()).toLocalDateTime());
        authenticationLog.setIpAddress(details.getRemoteAddress());
        authenticationLog.setSessionId(details.getSessionId());
        return authenticationLog;
    }

    private String getSourceUserName(Authentication authentication) {
        SwitchUserGrantedAuthority switchUserGrantedAuthority = authentication.getAuthorities()
                .stream()
                .filter(authority -> authority instanceof SwitchUserGrantedAuthority)
                .map(authority -> (SwitchUserGrantedAuthority) authority)
                .findFirst()
                .orElse(null);
        return switchUserGrantedAuthority != null ? switchUserGrantedAuthority.getSource().getName() : "";
    }

    private boolean hasAdminRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN));
    }

}