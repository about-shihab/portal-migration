package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUserIdOrUserNameAndIsActive(@NonNull String userId, @NonNull String userName, @NonNull Boolean isActive);

}