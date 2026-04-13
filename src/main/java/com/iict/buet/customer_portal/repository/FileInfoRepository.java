package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
}