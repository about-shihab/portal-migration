package com.iict.buet.customer_portal.controller;

import com.iict.buet.customer_portal.annotations.ApiController;
import com.iict.buet.customer_portal.annotations.DataValidation;
import com.iict.buet.customer_portal.client.dbbl.DbblClient;
import com.iict.buet.customer_portal.client.dbbl.dto.GetResultFieldDto;
import com.iict.buet.customer_portal.client.dbbl.dto.GetransidDto;
import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.service.BillInfoService;
import com.iict.buet.customer_portal.util.ResponseBuilder;
import com.iict.buet.customer_portal.util.UrlConstants;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

@ApiController
@RequestMapping(UrlConstants.BillInfoManagement.ROOT)
public class BillInfoController {
    private final BillInfoService billInfoService;
    private final DbblClient dbblClient;

    public BillInfoController(BillInfoService billInfoService, DbblClient dbblClient) {
        this.billInfoService = billInfoService;
        this.dbblClient = dbblClient;
    }

    @GetMapping(UrlConstants.BillInfoManagement.DOWNLOAD_FILE)
    public ResponseEntity<?> download(@RequestParam(name = "billMonth") Integer billMonth, @RequestParam(name = "billYear") Integer billYear) {
        return billInfoService.getMeteredCustomerBillPrint(billMonth, billYear);
    }

    @GetMapping(UrlConstants.BillInfoManagement.NON_METERED_BILL)
    public String getNonMeteredBillInfo(HttpServletResponse response, HttpServletRequest request) {
        return billInfoService.getNonMeteredCustomerBill();
    }

    @GetMapping(UrlConstants.BillInfoManagement.NON_METERED_PAYMENT_INFO)
    public Response getPaymentInfo(HttpServletResponse response, HttpServletRequest request) {
        return billInfoService.getNonMeterPaymentInfo();
    }

    @GetMapping(UrlConstants.BillInfoManagement.METERED_BILL)
    public String getMeteredBillInfo(HttpServletResponse response, HttpServletRequest request) {
        return billInfoService.getMeteredCustomerBill(false);
    }

    @GetMapping(UrlConstants.BillInfoManagement.METERED_BILL_ALL_DUES)
    public String getMeteredBillInfoAllDues(HttpServletResponse response, HttpServletRequest request) {
        return billInfoService.getMeteredCustomerBill(true);
    }

    @GetMapping(UrlConstants.BillInfoManagement.METERED_PARTIAL_BILL)
    public String getMeteredPartialBillInfo(HttpServletResponse response, HttpServletRequest request) {
        return billInfoService.getPartialMeteredCustomerBill();
    }

    @GetMapping(UrlConstants.BillInfoManagement.METERED_PARTIAL_DETAILS)
    public Response getMeteredPartialDetails() {
        return billInfoService.getPartialPaymentDetails();
    }

    @GetMapping(UrlConstants.BillInfoManagement.YEAR_MAP)
    public List<String> getYearMap(HttpServletResponse response, HttpServletRequest request) {
        return billInfoService.getYearList();
    }

    @GetMapping(UrlConstants.BillInfoManagement.BILL_LIST)
    public Response getBillList(@RequestParam("billYear") Long billYear, HttpServletRequest request, HttpServletResponse response) {
        return billInfoService.getCustomerBillList(billYear);
    }

    @GetMapping(UrlConstants.BillInfoManagement.BILL_HISTORY)
    public Response getBillHistory(@RequestParam(value = "start") @DateTimeFormat(pattern = "yyyy-MM") YearMonth start,
                                   @RequestParam(value = "end") @DateTimeFormat(pattern = "yyyy-MM") YearMonth end) {
        return billInfoService.getCustomerBillHistoryList(start, end);
    }

    @GetMapping(UrlConstants.BillInfoManagement.BILL_COLLECTION_REPORT)
    public ResponseEntity<?> getBillCollection(@RequestParam(value = "start") @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                               @RequestParam(value = "end") @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return billInfoService.getBillCollectionReport(start, end);
    }

    @GetMapping(UrlConstants.BillInfoManagement.BILL_LIST_UNPAID)
    public Response getBillList(HttpServletRequest request, HttpServletResponse response) {
        return billInfoService.getCustomerUnpaidBillList();
    }

    @PostMapping(UrlConstants.BillInfoManagement.GET_DBBL_PAYMENT_TRANSACTION_ID)
    @DataValidation
    public Response getGetransidResponse(@RequestBody GetransidDto getransidDto, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return dbblClient.getGetransidResponse(getransidDto, request);
    }

    @PutMapping(UrlConstants.BillInfoManagement.GET_DBBL_PAYMENT_RESULT)
    @DataValidation
    public Response getResultFieldResponse(@RequestBody GetResultFieldDto resultFieldDto, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return dbblClient.getResultFieldResponse(resultFieldDto, request);
    }

    @GetMapping(UrlConstants.BillInfoManagement.GATEWAY_FEE)
    public Response getBillList(@RequestParam("gatewayName") String gatewayName, @RequestParam("billAmount") BigDecimal billAmount, @RequestParam("cardType") String cardType, HttpServletRequest request, HttpServletResponse response) {
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, dbblClient.calculateBankFee(gatewayName, cardType, billAmount), gatewayName + " fee success");
    }
}
