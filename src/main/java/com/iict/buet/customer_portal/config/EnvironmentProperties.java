package com.iict.buet.customer_portal.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EnvironmentProperties {
    @Value("${collection-server}")
    private String COLLECTION_SERVER_URL;

    @Value("${collection-server-basic-auth}")
    private String COLLECTION_SERVER_BASIC_AUTH;

    @Value("${erp-server}")
    private String ERP_SERVER_URL;

    @Value("${erp-server-basic-auth}")
    private String ERP_SERVER_BASIC_AUTH;

    @Value("${dbbl-soap-url}")
    private String DBBL_SOAP_URL;

    @Value("${dbbl-gateway-client-url}")
    private String DBBL_GATEWAY_CLIENT_URL;

    @Value(("${dbbl-soap-username}"))
    private String DBBL_SOAP_USERNAME;

    @Value(("${dbbl-soap-password}"))
    private String DBBL_SOAP_PASSWORD;

    @Value(("${dbbl-collection-basic-auth}"))
    private String DBBL_COLLECTION_BASIC_AUTH;

    @Value(("${file.upload-dir}"))
    private String FILE_UPLOAD_DIR;
}
