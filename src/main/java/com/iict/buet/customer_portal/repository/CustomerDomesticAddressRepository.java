package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.CustomerDomesticAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDomesticAddressRepository extends JpaRepository<CustomerDomesticAddress, Long> {
    CustomerDomesticAddress findByCustomerIdAndTitleAndIsActiveTrue(Long customerId, String title);
}
