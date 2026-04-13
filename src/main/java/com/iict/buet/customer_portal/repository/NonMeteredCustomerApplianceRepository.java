package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.NonMeteredCustomerAppliance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NonMeteredCustomerApplianceRepository extends JpaRepository<NonMeteredCustomerAppliance, Long> {
    List<NonMeteredCustomerAppliance> findAllByCustomerId(Long customerId);
}
