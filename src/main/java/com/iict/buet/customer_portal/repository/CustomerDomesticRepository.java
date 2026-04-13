package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.CustomerDomestic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDomesticRepository extends JpaRepository<CustomerDomestic, Long> {
    CustomerDomestic findByCodeAndIsRegisteredTrue(String customerCode);
    int countByCodeAndIsRegisteredTrue(String customerCode);
    CustomerDomestic findByCodeAndMobileNoAndIsRegisteredTrue(String customerCode, String mobileNo);
}
