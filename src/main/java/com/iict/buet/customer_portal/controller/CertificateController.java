package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.service.CertificateService;
import com.iict.buet.customer_portal.util.UrlConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UrlConstants.CustomerCertificateManagement.ROOT)
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateService certificateService;

    @GetMapping(UrlConstants.CustomerCertificateManagement.DOWNLOAD)
    public ResponseEntity<?> downloadCertificate() {
        Long billYear = certificateService.getCertificateYear();
        return certificateService.prepareCertificate(12L, billYear);
    }
}
