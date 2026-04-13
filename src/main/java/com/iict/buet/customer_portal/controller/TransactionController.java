package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.annotations.ApiController;
import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.service.ProfileService;
import com.iict.buet.customer_portal.service.TransactionService;
import com.iict.buet.customer_portal.util.UrlConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApiController
@RequestMapping(UrlConstants.UserManagement.ROOT)
public class TransactionController {

    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }
    @GetMapping(UrlConstants.TransactionManagement.GET_ALL)
    public Response getTransactionInfo(HttpServletRequest request, HttpServletResponse response){
        return transactionService.getTransactionInfo();
    }
}
