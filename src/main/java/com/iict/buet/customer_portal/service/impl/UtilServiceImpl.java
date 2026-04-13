package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.dto.UserPrincipal;
import com.iict.buet.customer_portal.model.CustomerType;
import com.iict.buet.customer_portal.repository.CustomerDomesticRepository;
import com.iict.buet.customer_portal.repository.CustomerRepository;
import com.iict.buet.customer_portal.service.UtilService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static com.iict.buet.customer_portal.util.SystemConstants.CERT_TITLE;

@Service("utilService")
public class UtilServiceImpl implements UtilService {
    private static final Logger logger = LogManager.getLogger(UtilServiceImpl.class.getName());
    private final CustomerDomesticRepository customerDomesticRepository;
    private final CustomerRepository customerRepository;
    private final SimplePdfExporterConfiguration pdfExporterConfiguration;
    private final JRPdfExporter jrPdfExporter;

    @Value("${spring.datasource.jdbcUrl}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverName;
    private static final String REPORT_DIR = "REPORT_DIR";
    private static final String IMAGE_DIR = "IMAGE_DIR";
    private static final String SUBREPORT_DIR = "SUBREPORT_DIR";

    public UtilServiceImpl(CustomerDomesticRepository customerDomesticRepository, CustomerRepository customerRepository, SimplePdfExporterConfiguration pdfExporterConfiguration, JRPdfExporter jrPdfExporter) {
        this.customerDomesticRepository = customerDomesticRepository;
        this.customerRepository = customerRepository;
        this.pdfExporterConfiguration = pdfExporterConfiguration;
        this.jrPdfExporter = jrPdfExporter;
    }

    @Override
    public String getLoggedInUserCustomerCode() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getCustomerCode();
    }

    @Override
    public String getLoggedInUserMobileNo() {
        String customerCode = getLoggedInUserCustomerCode();
        if (customerCode.contains("NM")) {
            return customerDomesticRepository.findByCodeAndIsRegisteredTrue(customerCode).getMobileNo();
        }
        return customerRepository.findByCodeAndIsRegisteredTrue(customerCode).getMobileNo();
    }

    @Override
    public String getLoggedInUsername() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getUsername();
    }

    @Override
    public String getCustomerZone() {
        String customerCode = getLoggedInUserCustomerCode();
        if (customerCode.contains("NM-")) {
            return customerDomesticRepository.findByCodeAndIsRegisteredTrue(customerCode).getZone();
        }
        return customerRepository.findByCodeAndIsRegisteredTrue(customerCode).getZone();
    }

    @Override
    public Boolean isNonMeteredCustomer() {
        String customerCode = getLoggedInUserCustomerCode();
        return customerCode.contains("NM-");
    }

    @Override
    public CustomerType getCustomerType() {
        String customerCode = getLoggedInUserCustomerCode();
        if (customerCode.contains("NM-")) {
            return customerDomesticRepository.findByCodeAndIsRegisteredTrue(customerCode).getCustomerType();
        }
        return customerRepository.findByCodeAndIsRegisteredTrue(customerCode).getCustomerType();
    }

    @Override
    public ByteArrayResource getPdfReport(String jasperName, Map<String, Object> reportParams, String pdfTitle) throws SQLException, JRException, IOException {
        reportParams.put(SUBREPORT_DIR, "/home/buet/resource/reports");
        reportParams.put(REPORT_DIR, "/home/buet/resource/reports");
        reportParams.put(IMAGE_DIR, "/home/buet/resource/images");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Class.forName(driverName);
            JasperPrint print = JasperFillManager.fillReport("/home/buet/resource/reports" + File.separator + jasperName + ".jasper", reportParams, connection);
            return getByteArrayResource(getLoggedInUserCustomerCode(), print, pdfTitle);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<?> prepareCertificate(Map<String, Object> reportParams, String reportPath, String customerCode) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Resource reportDir = new ClassPathResource("reports/");
            Resource imageDir = new ClassPathResource("static/images/");

            reportParams.put(SUBREPORT_DIR, reportDir.getFile().getAbsolutePath());
            reportParams.put(REPORT_DIR, reportDir.getFile().getAbsolutePath());
            reportParams.put(IMAGE_DIR, imageDir.getFile().getAbsolutePath());

            Resource reportFile = new ClassPathResource("reports/" + reportPath);
            JasperPrint print = JasperFillManager.fillReport(reportFile.getInputStream(), reportParams, connection);

            ByteArrayResource resource = getByteArrayResource(customerCode, print, CERT_TITLE);

            HttpHeaders headers = new HttpHeaders();
            String fileName = customerCode + "_certificate" + ".pdf";
            headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());

            logger.info("Certificate generated for customer: {}", customerCode);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .headers(headers)
                    .body(resource);

        } catch (Exception ex) {
            logger.error("Specific Cause: {}", NestedExceptionUtils.getMostSpecificCause(ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request.");
        }
    }
    @Override
    public ResponseEntity<?> prepareReport(Map<String, Object> reportParams, InputStream reportStream, String customerCode, String reportType, String title) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            JasperPrint print = JasperFillManager.fillReport(reportStream, reportParams, connection);
            ByteArrayResource resource = getByteArrayResource(customerCode, print, title);
            HttpHeaders headers = new HttpHeaders();
            String fileName = customerCode + "_" + reportType + ".pdf";
            headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());

            logger.info("{} generated for: {}", reportType, customerCode);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .headers(headers)
                    .body(resource);

        } catch (Exception ex) {
            logger.error("Specific Cause: {}", NestedExceptionUtils.getMostSpecificCause(ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request.");
        }
    }


    private ByteArrayResource getByteArrayResource(String customerCode, JasperPrint print, String title) throws JRException {
        pdfExporterConfiguration.setMetadataSubject(customerCode);
        pdfExporterConfiguration.setMetadataTitle(title);
        jrPdfExporter.setConfiguration(pdfExporterConfiguration);

        SimpleExporterInput input = new SimpleExporterInput(print);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(baos);
        jrPdfExporter.setExporterInput(input);
        jrPdfExporter.setExporterOutput(output);
        jrPdfExporter.exportReport();

        byte[] pdfBytes = baos.toByteArray();
        output.close();
        return new ByteArrayResource(pdfBytes);
    }

}
