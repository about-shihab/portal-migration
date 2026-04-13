package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.dto.UserPrincipal;
import com.iict.buet.customer_portal.model.User;
import com.iict.buet.customer_portal.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String customerCode) throws UsernameNotFoundException {
        // Let people login with either username or email
        User user = userRepository.findByCustomerCodeAndIsActiveTrue(customerCode).orElseThrow(() -> new UsernameNotFoundException("User not found with code : " + customerCode));
        return UserPrincipal.create(user);
    }

    // This method is used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));
        return UserPrincipal.create(user);
    }
}