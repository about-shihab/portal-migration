package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    @GetMapping("/download/reconnection-application/{fileId}")
    public ResponseEntity<Resource> downloadReconnectionApplicationFile(
            @PathVariable Long fileId,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request) throws IOException {
        if (!isLocalNetworkRequest(request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return externalApiService.downloadReconnectionApplicationFile(fileId, authHeader);
    }

    private boolean isLocalNetworkRequest(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        return isLocalhost(remoteAddr) || isPrivateIPAddress(remoteAddr);
    }

    private boolean isPrivateIPAddress(String ipAddress) {
        final String[] parts = ipAddress.split("\\.");
        int[] ip = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ip[i] = Integer.parseInt(parts[i]);
        }
        return (ip[0] == 10) ||
                (ip[0] == 172 && (ip[1] >= 16 && ip[1] <= 31)) ||
                (ip[0] == 192 && ip[1] == 168);
    }

    private boolean isLocalhost(String ipAddress) {
        return "127.0.0.1".equals(ipAddress) || "::1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File operation failed: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}
