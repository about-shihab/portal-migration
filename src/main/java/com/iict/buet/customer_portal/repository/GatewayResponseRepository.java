package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.GatewayResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatewayResponseRepository extends JpaRepository<GatewayResponse, Long> {
    GatewayResponse findByResponseAndCustomerCodeAndGatewayNameAndIsSuccessTrueAndIsUsedFalse(String transactionId, String customerCode, String gatewayName);
}
