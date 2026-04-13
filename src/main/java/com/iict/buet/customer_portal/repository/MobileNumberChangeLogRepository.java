package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.MobileNumberChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Collectors;

public interface MobileNumberChangeLogRepository extends JpaRepository<MobileNumberChangeLog, Long> {
    default List<MobileNumberChangeLog> findByCustomerCodeAndYear(String customerCode, int year) {
        return findByCustomerCode(customerCode).stream()
                .filter(log -> log.getChangedDateTime().getYear() == year)
                .collect(Collectors.toList());
    }

    List<MobileNumberChangeLog> findByCustomerCode(String customerCode);
}
