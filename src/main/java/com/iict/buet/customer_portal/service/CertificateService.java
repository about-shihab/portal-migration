package com.iict.buet.customer_portal.service;

import org.springframework.http.ResponseEntity;

public interface CertificateService {
    ResponseEntity<?> prepareCertificate(Long billMonth, Long billYear);

    Long getCertificateYear();
}