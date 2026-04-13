package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.InstalledMeterInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstalledMeterInfoRepository extends JpaRepository<InstalledMeterInfo, Long> {
    List<InstalledMeterInfo> findAllByCustomerCodeAndIsActiveTrue(String customerCode);
}
