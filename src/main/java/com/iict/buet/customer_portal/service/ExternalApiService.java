package com.iict.buet.customer_portal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final FileService fileService;

    @Value("${external.api-auth-header}")
    private String authHeader;

    public ResponseEntity<Resource> downloadReconnectionApplicationFile(Long fileId, String authHeader) throws IOException {
        authenticate(authHeader);
        return fileService.buildFileResponse(fileId, false);
    }

    private void authenticate(String authHeader) {
        if (!this.authHeader.equals(authHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
