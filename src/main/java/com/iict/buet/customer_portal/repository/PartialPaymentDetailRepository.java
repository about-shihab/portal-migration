package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.PartialPaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartialPaymentDetailRepository extends JpaRepository<PartialPaymentDetail, Long> {
    public List<PartialPaymentDetail> findByCustCodeAndStatus(String custCode, String status);
}