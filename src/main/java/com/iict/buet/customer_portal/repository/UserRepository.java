package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// TODO: Question: Should this not be CustomerRepository?
public interface UserRepository extends JpaRepository<User, /*BigInteger*/Long> {
    User findByIdAndIsActiveTrue(Long id);

    Optional<User> findByCustomerCodeAndIsActiveTrue(String customerCode);
    User findUserByCustomerCodeAndIsActiveTrue(String customerCode);
    int countByCustomerCodeAndIsActiveTrue(String customerCode);
    int countByCustomerCodeAndIsActiveFalse(String customerCode);
    List<User> findAllByCustomerCodeAndIsActiveFalse(String customerCode);

    boolean existsByCustomerCode(String customerCode);

    User findFirstByCustomerCodeAndIsActiveFalseOrderByLastOtpSendDateDesc(String customerCode);

    List<User> findAllByUpdatedByIsNull();
}
