package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.dto.RoleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    Response create(RoleDto roleDto);
    Response update(Long id, RoleDto roleDto);
    Response delete(Long id);
    Response get(Long id);
    Response getAll(Pageable pageable, boolean isExport, String search, String status);
}
