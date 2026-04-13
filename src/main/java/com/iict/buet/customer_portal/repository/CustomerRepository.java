package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByCodeAndIsRegisteredTrue(String customerCode);
    int countByCodeAndIsRegisteredTrue(String customerCode);
    Customer findByCodeAndMobileNoAndIsRegisteredTrue(String customerCode, String mobileNo);
}
