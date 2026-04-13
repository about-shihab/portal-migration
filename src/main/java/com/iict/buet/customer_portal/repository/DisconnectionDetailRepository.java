package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.DisconnectionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisconnectionDetailRepository extends JpaRepository<DisconnectionDetail, Long> {
    DisconnectionDetail findFirstByCustCodeOrderByDisconnectionDateTimeDesc(String custCode);
}
