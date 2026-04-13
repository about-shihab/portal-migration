package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankRepository extends JpaRepository<Bank, Long> {
    List<Bank> findAllByIsActiveTrueAndUsernameNotNullAndNameNotOrderByFullNameAsc(String name);
    Bank findByNameAndIsActiveTrue(String name);

}
