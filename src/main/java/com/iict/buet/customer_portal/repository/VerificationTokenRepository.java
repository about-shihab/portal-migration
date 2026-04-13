package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.User;
import com.iict.buet.customer_portal.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
	VerificationToken findByToken(String token);
	VerificationToken findByTokenAndCustomerCodeAndIsActiveTrue(String token, String customerCode);
    List<VerificationToken> findAllByCustomerCode(String customerCode);
    List<VerificationToken> findAllByExpiryDateBeforeAndIsActiveTrue(Date date);
}
