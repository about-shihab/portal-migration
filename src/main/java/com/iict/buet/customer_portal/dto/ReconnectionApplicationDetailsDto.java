package com.iict.buet.customer_portal.dto;

import com.iict.buet.customer_portal.model.ReconnectionApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconnectionApplicationDetailsDto {
    private Long id;
    private String customerCode;
    private Date applicationDate;
    private Date disconnectionDate;
    private String zone;
    private ReconnectionApplication.Status status;
    private String remarks;
    private FileInfoDto attachmentFileInfo;
    private FileInfoDto appFileInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileInfoDto {
        private Long id;
        private String name;
        private String title;
        private String contentType;
        private Long size;
        private String path;
        private String url;
    }
}

