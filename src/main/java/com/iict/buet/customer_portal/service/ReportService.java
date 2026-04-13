package com.iict.buet.customer_portal.service;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.*;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ReportService {
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(ReportService.class);
    private final HtmlExporter htmlExporter;
    private final SimpleHtmlReportConfiguration htmlReportConfiguration;
    private final JRPdfExporter pdfExporter;
    private final SimplePdfExporterConfiguration pdfExporterConfig;

    private final DataSource dataSource;
    private static final String JASPER_EXTENSION = ".jasper";

    @Value("${report.base-dir:/reports}")
    private String reportBaseDir;


    public String getReportHtml(String reportName, Map<String, Object> parameters) {
        try {
            JasperPrint jasperPrint = fillReport(reportName, parameters);

            ByteArrayOutputStream htmlStream = new ByteArrayOutputStream();
            htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            htmlExporter.setExporterOutput(new SimpleHtmlExporterOutput(htmlStream));
            htmlExporter.setConfiguration(htmlReportConfiguration);

            htmlExporter.exportReport();

            logger.info("HTML report generated successfully.");

            return htmlStream.toString("UTF-8");
        } catch (JRException | UnsupportedEncodingException ex) {
            logger.error("Failed to generate HTML report.", ex);
            return "Failed to generate HTML report.";
        }
    }

    public byte[] exportReportPdfAsBytes(String reportName, Map<String, Object> parameters, String reportPdfTitle) {
        try {
            JasperPrint jasperPrint = fillReport(reportName, parameters);

            pdfExporterConfig.setMetadataTitle(reportPdfTitle);

            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfStream));
            pdfExporter.setConfiguration(pdfExporterConfig);

            pdfExporter.exportReport();

            logger.info("PDF report generated successfully.");

            return pdfStream.toByteArray();
        } catch (JRException ex) {
            logger.error("Failed to generate PDF report.", ex);
            throw new RuntimeException("Failed to generate PDF report.", ex);
        }
    }

    private JasperPrint fillReport(String reportName, Map<String, Object> parameters) throws JRException {
        String reportPath = reportBaseDir + File.separator + reportName + JASPER_EXTENSION;

        try (Connection connection = dataSource.getConnection();
             InputStream reportStream = getReportStream(reportPath)) {

            if (reportStream == null) {
                throw new JRException("Report not found in both file system and classpath: " + reportPath);
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            return JasperFillManager.fillReport(jasperReport, parameters, connection);

        } catch (Exception ex) {
            logger.error("Failed to fill report: " + reportName, ex);
            throw new JRException("Failed to fill report: " + reportName, ex);
        }
    }

    private InputStream getReportStream(String reportPath) {
        try {
            File reportFile = new File(reportPath);
            if (reportFile.exists()) {
                return new FileInputStream(reportFile);
            } else {
                return getClass().getResourceAsStream(reportPath);
            }
        } catch (Exception ex) {
            logger.error("Error while loading report: " + reportPath, ex);
            return null;
        }
    }
}
