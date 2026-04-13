package com.iict.buet.customer_portal.config;

import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.iict.buet.customer_portal.util.SystemConstants.AUTHOR;
import static com.iict.buet.customer_portal.util.SystemConstants.CREATOR;

@Configuration
public class ReportConfiguration {
    @Bean
    public SimplePdfExporterConfiguration simplePdfExporterConfiguration() {
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setMetadataAuthor(AUTHOR);
        configuration.setDisplayMetadataTitle(true);
        configuration.setMetadataCreator(CREATOR);
        return configuration;
    }

    @Bean
    public JRPdfExporter jrPdfExporter() {
        return new JRPdfExporter();
    }

    @Bean
    public SimpleHtmlReportConfiguration simpleHtmlReportConfiguration() {
        SimpleHtmlReportConfiguration configuration = new SimpleHtmlReportConfiguration();
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setWhitePageBackground(false);
        return configuration;
    }

    @Bean
    public HtmlExporter htmlExporter() {
        return new HtmlExporter();
    }
}
