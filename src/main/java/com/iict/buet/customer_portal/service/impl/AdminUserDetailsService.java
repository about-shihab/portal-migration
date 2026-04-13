package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.model.AdminUser;
import com.iict.buet.customer_portal.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.iict.buet.customer_portal.util.SystemConstants.ROLE_ADMIN;

@RequiredArgsConstructor
@Service("adminUserDetailsService")
public class AdminUserDetailsService implements UserDetailsService {
    private final AdminUserRepository adminUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserRepository.findByUserIdOrUserNameAndIsActive(username, username, true)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username : " + username));
        return User.withUsername(adminUser.getUserId())
                .password(adminUser.getPassword())
                .authorities(ROLE_ADMIN)
                .accountLocked(!adminUser.getIsActive())
                .build();
    }
}
