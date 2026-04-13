package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.dto.ReconnectionApplicationDetailsDto;
import com.iict.buet.customer_portal.dto.ReconnectionApplicationDto;
import com.iict.buet.customer_portal.model.ReconnectionApplication;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ReconnectionService {
    ReconnectionApplicationDto getReconnectionInfo();

    boolean isTemporaryDisconnected(String customerCode);

    String createReconnectionApplication(MultipartFile billFile);

    boolean isReconnectionPending();

    String eligibleForReconnectionMsg();

    Optional<ReconnectionApplication> getReconnectionApplication();

    ReconnectionApplicationDetailsDto toDto(ReconnectionApplication reconnectionApplication);

    String getReconnectionAppReportHtml();
}
