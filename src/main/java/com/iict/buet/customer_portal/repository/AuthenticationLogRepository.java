package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.AuthenticationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface AuthenticationLogRepository extends JpaRepository<AuthenticationLog, Long> {
    @Transactional
    @Modifying
    @Query("update CUSTOMER_PORTAL_AUTH_LOG s set s.logoutTime = :logoutTime where s.sessionId = :sessionId and s.logoutTime is null")
    void updateLogout(LocalDateTime logoutTime, String sessionId);
}