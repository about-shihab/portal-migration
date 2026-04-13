package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.model.CertificateIssueLog;
import com.iict.buet.customer_portal.repository.CertificateIssueLogRepository;
import com.iict.buet.customer_portal.repository.CustomerBillRepository;
import com.iict.buet.customer_portal.service.CertificateService;
import com.iict.buet.customer_portal.service.UtilService;
import com.iict.buet.customer_portal.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.iict.buet.customer_portal.util.SystemConstants.DUES_CERT_PATH;
import static com.iict.buet.customer_portal.util.SystemConstants.NO_DUES_CERT_PATH;

@Service("CertificateService")
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final UtilService utilService;
    private final CustomerBillRepository customerBillRepository;
    private final CertificateIssueLogRepository certificateIssueLogRepository;
    private final AuthUtils authUtils;

    @Override
    public ResponseEntity<?> prepareCertificate(Long billMonth, Long billYear) {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String reportPath = NO_DUES_CERT_PATH;

        if (customerBillRepository.getCustomerCertificateDeterminer(customerCode, billYear, billMonth) > 0) {
            reportPath = DUES_CERT_PATH;
        }
        Map<String, Object> reportParams = new HashMap<>();
        reportParams.put("inBILL_YEAR", billYear);
        reportParams.put("inBILL_MONTH", billMonth);
        reportParams.put("inCUST_CODE", customerCode);

        ResponseEntity<?> response = utilService.prepareCertificate(reportParams, reportPath, customerCode);

        if (response.getStatusCode() == HttpStatus.OK) {
            CertificateIssueLog log = new CertificateIssueLog();
            log.setCustomerCode(customerCode);
            log.setIssueDateTime(LocalDateTime.now());
            if (authUtils.canUserImpersonateCustomer()) {
                String impersonateUser = authUtils.getImpersonatedUser();
                log.setIssuedBy(impersonateUser);
                log.setReason("Certificate initialized by " + impersonateUser);
                log.setRemarks("Certificate generated from portal");
            } else {
                log.setIssuedBy(utilService.getLoggedInUserCustomerCode());
                log.setReason("Customer Initialized");
                log.setRemarks("Certificate generated from portal");
            }
            certificateIssueLogRepository.save(log);
        }
        return response;
    }

    @Override
    public Long getCertificateYear() {
        int correntMonth = LocalDate.now().getMonthValue();
        Long billYear = (long) LocalDate.now().getYear() - 1;
        return billYear;
    }
}
