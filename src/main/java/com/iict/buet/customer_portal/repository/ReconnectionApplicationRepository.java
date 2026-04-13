package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.ReconnectionApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReconnectionApplicationRepository extends JpaRepository<ReconnectionApplication, Long> {

    Optional<ReconnectionApplication> findByCustomerCodeAndStatusOrderByApplicationDateDesc(String customerCode, ReconnectionApplication.Status status);
}