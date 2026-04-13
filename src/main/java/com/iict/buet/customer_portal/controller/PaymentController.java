package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.client.dbbl.DbblClient;
import com.iict.buet.customer_portal.client.dbbl.dto.GetResultFieldDto;
import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.service.UiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@CrossOrigin(origins = {
        "https://ecomtest.dutchbanglabank.com",
        "https://ecom.dutchbanglabank.com",
        "https://ecom1.dutchbanglabank.com"
})
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final DbblClient dbblClient;
    private final UiService uiService;

    @ModelAttribute
    public void setCommonAttributes(Model model, HttpServletRequest request, Principal principal) {
        uiService.setCommonAttributes(model, request, principal);
    }

    @PostMapping(value = "/dbbl/success", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String dbblReturnMapping(HttpServletRequest request, Model model) {
        String trans_id = request.getParameter("trans_id");
        GetResultFieldDto getResultFieldDto = new GetResultFieldDto();
        getResultFieldDto.setTransid(trans_id);
        Response response = dbblClient.getResultFieldResponse(getResultFieldDto, request);

        if (response.getStatusCode() == 200) {
            model.addAttribute("paymentConfirmation", "Payment Successful!");
            model.addAttribute("paymentDetails", response.getContent());
        } else {
            model.addAttribute("paymentConfirmation", "Payment Failed!");
            model.addAttribute("paymentError", response.getMessage());
        }
        return "bill/dbbl-success";
    }
}
