package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.DuplicateCardIssueLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DuplicateCardIssueLogRepository extends JpaRepository<DuplicateCardIssueLog, Long> {
}
