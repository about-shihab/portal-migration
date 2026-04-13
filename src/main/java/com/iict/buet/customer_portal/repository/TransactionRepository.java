package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByCustomerCode(String customerCode);
}
