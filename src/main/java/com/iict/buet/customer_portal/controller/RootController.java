package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.service.CertificateService;
import com.iict.buet.customer_portal.service.UiService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class RootController {
    private final UiService uiService;
    private final CertificateService certificateService;

    @AllArgsConstructor
    @Getter
    private static class PaymentMethod {
        String name;
        String image;
        Integer cardType;
        String description;
        boolean isChecked;
    }

    @ModelAttribute
    public void setCommonAttributes(Model model, HttpServletRequest request, Principal principal) {
        uiService.setCommonAttributes(model, request, principal);
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "auth/signup";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "auth/forgot-password";
    }

    @GetMapping("/profile")
    public String showProfilePage() {
        return "profile";
    }

    @GetMapping("/bill-details")
    public String showBillDetailsPage() {
        return "bill/details";
    }

    @GetMapping("/pay-bill")
    public String showPayBillPage(Model model) {
        model.addAttribute("paymentMethods", getPaymentMethods());
        return "bill/pay-bill";
    }

    @GetMapping("/bill-history")
    public String showBillHistoryPage() {
        return "bill/history";
    }

    @GetMapping("/bill-collection")
    public String showBillCollectionPage() {
        return "bill/collection";
    }

    @GetMapping("/change-password")
    public String showChangePasswordPage() {
        return "change-password";
    }

    //hide complain menu item

//    @GetMapping("/create-complain")
//    public String showCreateComplain() {
//        return "complain/create";
//    }
//
//    @GetMapping("/complain-review")
//    public String showComplainReview() {
//        return "complain/review";
//    }
//
//    @GetMapping("/ticket-status-list")
//    public String showTicketStatusList() {
//        return "complain/ticket-status-list";
//    }
//
//    @GetMapping("/ticket-review-list")
//    public String showTicketReviewList() {
//        return "complain/ticket-review-list";
//    }

    @GetMapping("/certificate")
    public String showCertificate(Model model) {
        Long billYear = certificateService.getCertificateYear();
        model.addAttribute("billYear", billYear);
        return "certificate";
    }

    @GetMapping("/registration-card")
    public String showRegistrationCard() {
        return "registration-card";
    }


    @GetMapping("/")
    public String rootMapping() {
        return "redirect:/profile";
    }

    @GetMapping("/impersonate")
    public String showImpersonatePage() {
        return "impersonate";
    }

    private List<PaymentMethod> getPaymentMethods() {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        paymentMethods.add(new PaymentMethod("DBBL Nexus", "static/images/pay-bill/dbbl_nexsus_logo_edited.png", 1, "TK 10/- Per Transaction", true));
        paymentMethods.add(new PaymentMethod("DBBL Master", "static/images/pay-bill/dbbl_master_logo_edited.png", 2, "1.50% of Transaction Amount", false));
        paymentMethods.add(new PaymentMethod("DBBL Visa", "static/images/pay-bill/dbbl_visa_logo_edited.png", 3, "1.50% of Transaction Amount", false));
        paymentMethods.add(new PaymentMethod("Visa", "static/images/pay-bill/visa_card_logo.png", 4, "1.50% of Transaction Amount", false));
        paymentMethods.add(new PaymentMethod("Master Card", "static/images/pay-bill/master_card_logo.png", 5, "1.50% of Transaction Amount", false));
        paymentMethods.add(new PaymentMethod("Rocket", "static/images/pay-bill/dbbl_rocket_logo.png", 6, "1.00% of Transaction Amount", false));
        return paymentMethods;
    }

}
