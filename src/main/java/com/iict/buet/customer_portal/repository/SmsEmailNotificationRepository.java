package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.SmsEmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsEmailNotificationRepository extends JpaRepository<SmsEmailNotification, Long> {
}
