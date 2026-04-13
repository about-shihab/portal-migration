package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.config.EnvironmentProperties;
import com.iict.buet.customer_portal.dto.ProfileDto;
import com.iict.buet.customer_portal.dto.ReconnectionApplicationDetailsDto;
import com.iict.buet.customer_portal.dto.ReconnectionApplicationDto;
import com.iict.buet.customer_portal.model.*;
import com.iict.buet.customer_portal.repository.CustomerDomesticRepository;
import com.iict.buet.customer_portal.repository.CustomerRepository;
import com.iict.buet.customer_portal.repository.DisconnectionDetailRepository;
import com.iict.buet.customer_portal.repository.ReconnectionApplicationRepository;
import com.iict.buet.customer_portal.service.*;
import com.iict.buet.customer_portal.util.AuthUtils;
import com.iict.buet.customer_portal.util.ByteArrayMultipartFile;
import com.iict.buet.customer_portal.util.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReconnectionServiceImpl implements ReconnectionService {
    private final UtilService utilService;
    private final ProfileService profileService;
    private final DisconnectionDetailRepository disconnectionDetailRepository;
    private final BillInfoService billInfoService;
    private final ReconnectionApplicationRepository reconnectionApplicationRepository;
    private final AuthUtils authUtils;
    private final CustomerDomesticRepository customerDomesticRepository;
    private final CustomerRepository customerRepository;
    private final FileService fileService;
    private final EnvironmentProperties envProperties;
    private final ReportService reportService;
    private static final String RECONNECTION_APP_SUBFOLDER = "/reconnection";
    private static final String RECONNECTION_APP_TITLE = "Reconnection Application";
    private static final String RECONNECTION_APP_REPORT_NAME = "reconnection_application";

    @Override
    public ReconnectionApplicationDto getReconnectionInfo() {
        ProfileDto profileDto = profileService.getProfile();
        ReconnectionApplicationDto reconnectionApplicationDto = new ReconnectionApplicationDto();
        reconnectionApplicationDto.setProfile(profileDto);
        DisconnectionDetail disconnectionDetail = disconnectionDetailRepository
                .findFirstByCustCodeOrderByDisconnectionDateTimeDesc(profileDto.getCustomerCode());
        if (disconnectionDetail != null) {
            reconnectionApplicationDto.setDisconnectionDate(disconnectionDetail.getDisconnectionDateTime());
            reconnectionApplicationDto.setDisconnectionCauses(disconnectionDetail.getCauses());
        }
        reconnectionApplicationDto.setDueBillExist(billInfoService.isDueBillExist());
        reconnectionApplicationDto.setDisconnected(isTemporaryDisconnected(profileDto.getCustomerCode()));
        return reconnectionApplicationDto;
    }

    @Override
    public boolean isTemporaryDisconnected(String customerCode) {
        String connectionStatusId;
        if (customerCode.contains("NM-")) {
            CustomerDomestic customerDomestic = customerDomesticRepository.findByCodeAndIsRegisteredTrue(customerCode);
            connectionStatusId = customerDomestic.getConnectionStatusType().getId().toString();
        } else {
            Customer customer = customerRepository.findByCodeAndIsRegisteredTrue(customerCode);
            connectionStatusId = customer.getConnectionStatusType().getId().toString();
        }
        return SystemConstants.ELIGIBLE_FOR_RECONNECTION.contains(connectionStatusId);
    }

    @Override
    @Transactional
    public String createReconnectionApplication(MultipartFile billFile) {
        try {
            if (authUtils.canUserImpersonateCustomer()) {
                return "Admin users are not allowed to submit reconnection application.";
            }
            String eligibilityMessage = eligibleForReconnectionMsg();
            if (!eligibilityMessage.isEmpty()) {
                return eligibilityMessage;
            }

            ReconnectionApplicationDto reconnectionApplicationDto = getReconnectionInfo();
            String customerCode = reconnectionApplicationDto.getProfile().getCustomerCode();
            ReconnectionApplication reconnectionApplication = buildReconnectionApplication(customerCode, reconnectionApplicationDto);
            reconnectionApplication.setAppFileInfo(storeReconnectionAppPdfAndGetFileInfo());
            reconnectionApplication.setAttachmentFileInfo(storeReconnectionAttachmentAndGetFileInfo(billFile));

            reconnectionApplicationRepository.save(reconnectionApplication);
            return "You have successfully submitted reconnection application. We will get back to you soon.";
        } catch (Exception e) {
            return "Failed to submit reconnection application. Please try again later.";
        }
    }

    @Override
    public boolean isReconnectionPending() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        return reconnectionApplicationRepository.findByCustomerCodeAndStatusOrderByApplicationDateDesc(
                customerCode, ReconnectionApplication.Status.PENDING).isPresent();
    }

    @Override
    public String eligibleForReconnectionMsg() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        if (isReconnectionPending())
            return "Your reconnection application is being processed. Please wait for approval.";
        else if (!isTemporaryDisconnected(customerCode))
            return "You are not eligible for reconnection.";
        else if (billInfoService.isDueBillExist()) {
            if (billInfoService.isBlockBillExist())
                return "You have unpaid bills. To learn more about your unpaid bill, please contact the respective revenue section in KGDCL";
            return "You have unpaid bills. Please pay your Unpaid Bills first!";
        } else
            return "";
    }

    @Override
    public Optional<ReconnectionApplication> getReconnectionApplication() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        return reconnectionApplicationRepository.findByCustomerCodeAndStatusOrderByApplicationDateDesc(
                customerCode, ReconnectionApplication.Status.PENDING);
    }

    @Override
    public ReconnectionApplicationDetailsDto toDto(ReconnectionApplication application) {
        ReconnectionApplicationDetailsDto dto = new ReconnectionApplicationDetailsDto();
        dto.setId(application.getId());
        dto.setCustomerCode(application.getCustomerCode());
        dto.setApplicationDate(application.getApplicationDate());
        dto.setDisconnectionDate(application.getDisconnectionDate());
        dto.setZone(application.getZone());
        dto.setStatus(application.getStatus());
        dto.setRemarks(application.getRemarks());
        dto.setAttachmentFileInfo(toFileInfoDto(application.getAttachmentFileInfo()));
        dto.setAppFileInfo(toFileInfoDto(application.getAppFileInfo()));
        return dto;
    }

    @Override
    public String getReconnectionAppReportHtml() {
        return reportService.getReportHtml(RECONNECTION_APP_REPORT_NAME, getReconnectionReportParams());
    }

    private static ReconnectionApplication buildReconnectionApplication(String customerCode, ReconnectionApplicationDto reconnectionApplicationDto) {
        ReconnectionApplication reconnectionApplication = new ReconnectionApplication();
        reconnectionApplication.setCustomerCode(customerCode);
        reconnectionApplication.setDisconnectionDate(reconnectionApplicationDto.getDisconnectionDate());
        reconnectionApplication.setApplicationDate(new Date());
        reconnectionApplication.setStatus(ReconnectionApplication.Status.PENDING);
        reconnectionApplication.setZone(reconnectionApplicationDto.getProfile().getZone());
        reconnectionApplication.setRemarks("Reconnection application from customer portal");
        return reconnectionApplication;
    }

    private FileInfo storeReconnectionAttachmentAndGetFileInfo(MultipartFile billFile) throws IOException {
        String filePath = fileService.storeFile(billFile, Paths.get(envProperties.getFILE_UPLOAD_DIR() + RECONNECTION_APP_SUBFOLDER), true);
        return fileService.saveFileInfo(billFile, filePath, "Bill Copy");
    }

    private FileInfo storeReconnectionAppPdfAndGetFileInfo() throws IOException {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String fileName = "Reconnection_Application_" + customerCode + ".pdf";
        Path baseDir = Paths.get(envProperties.getFILE_UPLOAD_DIR() + RECONNECTION_APP_SUBFOLDER).toAbsolutePath().normalize();
        byte[] pdfContent = reportService.exportReportPdfAsBytes(RECONNECTION_APP_REPORT_NAME, getReconnectionReportParams(), RECONNECTION_APP_TITLE);
        MultipartFile pdfFile = new ByteArrayMultipartFile(pdfContent, fileName, MediaType.APPLICATION_PDF_VALUE);
        String filePath = fileService.storeFile(pdfFile, baseDir, true);
        return fileService.saveFileInfo(pdfFile, filePath, RECONNECTION_APP_TITLE);
    }

    private static ReconnectionApplicationDetailsDto.FileInfoDto toFileInfoDto(FileInfo fileInfo) {
        if (fileInfo == null) return null;
        return new ReconnectionApplicationDetailsDto.FileInfoDto(
                fileInfo.getId(),
                fileInfo.getName(),
                fileInfo.getTitle(),
                fileInfo.getContentType(),
                fileInfo.getSize(),
                fileInfo.getPath(),
                fileInfo.getUrl()
        );
    }

    private Map<String, Object> getReconnectionReportParams() {
        Map<String, Object> reportParams = new HashMap<>();
        reportParams.put("CUST_CODE", utilService.getLoggedInUserCustomerCode());
        reportParams.put("ATTACHED_NAME", "বিলের কপি");
        return reportParams;
    }
}
