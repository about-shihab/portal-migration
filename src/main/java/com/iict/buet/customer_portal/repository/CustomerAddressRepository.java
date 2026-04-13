package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
    CustomerAddress findByCustomerIdAndTitleAndIsActiveTrue(Long customerId, String title);
}
