package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.util.AuthUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UiService {
    private final AuthUtils authUtils;
    private final ReconnectionService reconnectionService;

    @Value("${app.version:0.0.0}")
    private String version;

    @AllArgsConstructor
    @Getter
    private static class MenuItem {
        String name;
        String url;
        String icon;
        boolean active;
        List<MenuItem> children;
    }

    public void setCommonAttributes(Model model, HttpServletRequest request, Principal principal) {
        if (principal != null) {
            String customerCode = principal.getName().toUpperCase();
            if (!customerCode.isEmpty()) {
                model.addAttribute("isNonMeterCustomer", customerCode.contains("NM"));
            }
            List<MenuItem> menuItems = createMenuItems(request, customerCode);
            model.addAttribute("menuItems", menuItems);
            model.addAttribute("loggedInUser", authUtils.getLoggedInUser());
            model.addAttribute("isImpersonating", authUtils.canUserImpersonateCustomer());
        }
        model.addAttribute("version", "v" + version);
    }

    private List<MenuItem> createMenuItems(HttpServletRequest request, String customerCode) {
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Customer Info", "/profile", "fas fa-user", isMenuItemActive(request, "/profile"), null));
        menuItems.addAll(createBillMenuItem(request));
//        menuItems.addAll(createComplaintMenuItem(request)); //hide complain menu item
        menuItems.add(new MenuItem("Certificate", "/certificate", "fas fa-certificate", isMenuItemActive(request, "/certificate"), null));
        if (!authUtils.hasOnlyAdminRole() && !customerCode.isEmpty() && reconnectionService.isTemporaryDisconnected(customerCode)) {
            menuItems.add(new MenuItem("Reconnection", "/reconnection", "fas fa-gas-pump", isMenuItemActive(request, "/reconnection"), null));
        }
        menuItems.add(new MenuItem("Change Password", "/change-password", "fas fa-key", isMenuItemActive(request, "/change-password"), null));
        menuItems.add(new MenuItem("Online Registration Card", "/registration-card", "fas fa-user", isMenuItemActive(request, "/registration-card"), null));


        if (authUtils.canUserImpersonateCustomer()) {
            MenuItem impersonateItem = new MenuItem("Impersonate Customer", "/impersonate", "fas fa-user-secret", isMenuItemActive(request, "/impersonate"), null);
            if (authUtils.hasOnlyAdminRole()) {
                menuItems = Collections.singletonList(impersonateItem);
            } else {
                menuItems.add(impersonateItem);
            }
        }
        return menuItems;
    }

    private List<MenuItem> createBillMenuItem(HttpServletRequest request) {
        List<MenuItem> billSubMenuItem = Arrays.asList(
                new MenuItem("Check", "/bill-details", "fas fa-file-invoice-dollar", isMenuItemActive(request, "/bill-details"), null),
                new MenuItem("History", "/bill-history", "fas fa-history", isMenuItemActive(request, "/bill-history"), null),
                new MenuItem("Collection", "/bill-collection", "fas fa-money-bill-wave", isMenuItemActive(request, "/bill-collection"), null)
        );
        return Collections.singletonList(new MenuItem("Bill", "#", "fas fa-file-invoice-dollar", billSubMenuItem.stream().anyMatch(MenuItem::isActive), billSubMenuItem));
    }

    private List<MenuItem> createComplaintMenuItem(HttpServletRequest request) {
        List<MenuItem> complaintSubMenuItem = Arrays.asList(
                new MenuItem("Create", "/create-complain", "fas fa-comment", isMenuItemActive(request, "/create-complain"), null),
                new MenuItem("Review", "/complain-review", "fas fa-check-circle", isMenuItemActive(request, "/complain-review"), null),
                new MenuItem("Ticket Status List", "/ticket-status-list", "fas fa-ticket-alt", isMenuItemActive(request, "/ticket-status-list"), null),
                new MenuItem("Ticket Review List", "/ticket-review-list", "fas fa-list-alt", isMenuItemActive(request, "/ticket-review-list"), null)
        );
        return Collections.singletonList(new MenuItem("Complain", "#", "fas fa-comment", complaintSubMenuItem.stream().anyMatch(MenuItem::isActive), complaintSubMenuItem));
    }

    private boolean isMenuItemActive(HttpServletRequest request, String url) {
        return request.getRequestURI().equals(request.getContextPath() + url);
    }
}
