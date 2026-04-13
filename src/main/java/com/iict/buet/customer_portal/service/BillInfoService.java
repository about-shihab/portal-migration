package com.iict.buet.customer_portal.service;

import com.iict.buet.customer_portal.dto.Response;
import org.springframework.http.ResponseEntity;

import java.time.YearMonth;
import java.util.Date;
import java.util.List;

public interface BillInfoService {
    ResponseEntity<?> getMeteredCustomerBillPrint(Integer billMonth, Integer billYear);

    String getNonMeteredCustomerBill();

    String getPartialMeteredCustomerBill();

    Response getNonMeterPaymentInfo();

    String getMeteredCustomerBill(Boolean isAllBill);

    Response getCustomerBillList(Long year);

    Response getCustomerBillHistoryList(YearMonth startDate, YearMonth endDate);

    ResponseEntity<?> getBillCollectionReport(Date startDate, Date endDate);

    Response getBankList();

    List<String> getYearList();

    Response getCustomerUnpaidBillList();

    Response getPartialPaymentDetails();

    boolean isDueBillExist();

    boolean isBlockBillExist();
}