package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.model.CustomerType;
import net.sf.jasperreports.engine.JRException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;

public interface UtilService {
    String getLoggedInUserCustomerCode();

    String getLoggedInUserMobileNo();

    String getLoggedInUsername();

    String getCustomerZone();

    Boolean isNonMeteredCustomer();

    CustomerType getCustomerType();

    ByteArrayResource getPdfReport(String jasperName, Map<String, Object> params, String pdfTitle) throws SQLException, JRException, IOException;

    ResponseEntity<?> prepareCertificate(Map<String, Object> reportParams, String reportPath, String customerCode);

    ResponseEntity<?> prepareReport(Map<String, Object> reportParams, InputStream reportStream, String customerCode, String reportType, String title);
}
