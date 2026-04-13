package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, /*BigInteger*/ Long> {
    Role findByName(String roleName);
    int countByName(String roleName);
    Role findByIdAndIsActiveTrue(Long id);
}
