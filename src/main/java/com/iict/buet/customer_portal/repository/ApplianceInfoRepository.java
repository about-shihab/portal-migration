package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.ApplianceInfo;
import com.iict.buet.customer_portal.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplianceInfoRepository extends JpaRepository<ApplianceInfo, Long> {
    ApplianceInfo findByIdAndIsActiveTrue(Long id);
}
