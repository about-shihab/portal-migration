package com.iict.buet.customer_portal.config;

import com.iict.buet.customer_portal.service.impl.AdminUserDetailsService;
import com.iict.buet.customer_portal.service.impl.CustomUserDetailsService;
import com.iict.buet.customer_portal.util.UrlConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import static com.iict.buet.customer_portal.util.SystemConstants.ROLE_ADMIN;
import static org.springframework.security.web.authentication.switchuser.SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService customUserDetailsService;
    private final AdminUserDetailsService adminUserDetailsService;

    private static final String[] publicEndpoints = {
            "/static/**",
            "/adminlte/**",
            "/login/**",
            "/signup/**",
            "/forgot-password/**",
            "/favicon.ico",
            "/api/external/**",
            UrlConstants.AuthManagement.ROOT + "/**"
    };

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        // add the admin auth provider
        authenticationManagerBuilder.authenticationProvider(adminAuthenticationProvider());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringAntMatchers("/api/**", "/payment/**", "/signup/**"))
                .authorizeRequests().antMatchers(publicEndpoints).permitAll()
                .antMatchers("/impersonate/**").hasAnyAuthority(ROLE_ADMIN, ROLE_PREVIOUS_ADMINISTRATOR)
                .anyRequest().authenticated().and()
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("custCode")
                        .successHandler((request, response, authentication) -> {
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(authority -> ROLE_ADMIN.equals(authority.getAuthority()))) {
                                response.sendRedirect("/impersonate");
                            } else {
                                SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                                if (savedRequest != null) {
                                    response.sendRedirect(savedRequest.getRedirectUrl());
                                } else {
                                    response.sendRedirect("/profile");
                                }
                            }
                        }))
                .sessionManagement(session -> session.maximumSessions(2).expiredUrl("/login?expired"));
    }

    @Bean
    public SwitchUserFilter switchUserFilter() {
        SwitchUserFilter switchUserFilter = new SwitchUserFilter();
        switchUserFilter.setUserDetailsService(customUserDetailsService);
        switchUserFilter.setSwitchUserUrl("/impersonate");
        switchUserFilter.setSwitchFailureUrl("/impersonate?error");
        switchUserFilter.setTargetUrl("/profile");
        return switchUserFilter;
    }

    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}