package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.dto.ReconnectionApplicationDetailsDto;
import com.iict.buet.customer_portal.model.ReconnectionApplication;
import com.iict.buet.customer_portal.service.FileService;
import com.iict.buet.customer_portal.service.ReconnectionService;
import com.iict.buet.customer_portal.service.UiService;
import com.iict.buet.customer_portal.util.UrlConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping(UrlConstants.ReconnectionManagement.ROOT)
@RequiredArgsConstructor
public class ReconnectionController {

    private final ReconnectionService reconnectionService;
    private final UiService uiService;
    private final FileService fileService;

    @ModelAttribute
    public void setCommonAttributes(Model model, HttpServletRequest request, Principal principal) {
        uiService.setCommonAttributes(model, request, principal);
    }

    @GetMapping("")
    public String getReconnectionInfo(Model model, @ModelAttribute("message") String message) {
        if (message == null || message.isEmpty()) {
            message = reconnectionService.eligibleForReconnectionMsg();
        }

        model.addAttribute("msg", message);

        Optional<ReconnectionApplication> optionalApplication = reconnectionService.getReconnectionApplication();
        if (optionalApplication.isPresent()) {
            ReconnectionApplication reconnectionApplication = optionalApplication.get();
            ReconnectionApplicationDetailsDto dto = reconnectionService.toDto(reconnectionApplication);
            model.addAttribute("reconnectionApplication", dto);
        } else {
            String htmlReport = reconnectionService.getReconnectionAppReportHtml();
            model.addAttribute("reconnectionContentHtml", htmlReport);
        }

        return "reconnection-application";
    }


    @PostMapping(UrlConstants.ReconnectionManagement.REQUEST)
    public String requestForReconnection(@RequestParam("billFile") MultipartFile billFile,
                                         Model model, RedirectAttributes redirectAttributes) {
        String message = reconnectionService.createReconnectionApplication(billFile);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/reconnection";
    }

    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            return fileService.buildFileResponse(fileId, true);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download file", e);
        }
    }
}