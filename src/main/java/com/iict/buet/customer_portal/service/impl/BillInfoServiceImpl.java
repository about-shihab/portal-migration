package com.iict.buet.customer_portal.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iict.buet.customer_portal.config.EnvironmentProperties;
import com.iict.buet.customer_portal.dto.*;
import com.iict.buet.customer_portal.exceptions.InvalidMonthException;
import com.iict.buet.customer_portal.model.Bank;
import com.iict.buet.customer_portal.model.CustomerBill;
import com.iict.buet.customer_portal.model.PartialPaymentDetail;
import com.iict.buet.customer_portal.repository.BankRepository;
import com.iict.buet.customer_portal.repository.CustomerBillRepository;
import com.iict.buet.customer_portal.repository.PartialPaymentDetailRepository;
import com.iict.buet.customer_portal.service.BillInfoService;
import com.iict.buet.customer_portal.service.UtilService;
import com.iict.buet.customer_portal.util.DateUtils;
import com.iict.buet.customer_portal.util.HttpUrlConnectionUtil;
import com.iict.buet.customer_portal.util.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service("billInfoService")
public class BillInfoServiceImpl implements BillInfoService {
    private static final Logger logger = LogManager.getLogger(BillInfoServiceImpl.class.getName());
    private final UtilService utilService;
    private final CustomerBillRepository customerBillRepository;
    private final BankRepository bankRepository;
    private final PartialPaymentDetailRepository partialPaymentDetailRepository;
    private final ModelMapper modelMapper;
    private final EnvironmentProperties envProps;

    public BillInfoServiceImpl(BankRepository bankRepository, CustomerBillRepository customerBillRepository, UtilService utilService, PartialPaymentDetailRepository partialPaymentDetailRepository, ModelMapper modelMapper, EnvironmentProperties envProps) {
        this.utilService = utilService;
        this.customerBillRepository = customerBillRepository;
        this.bankRepository = bankRepository;
        this.partialPaymentDetailRepository = partialPaymentDetailRepository;
        this.modelMapper = modelMapper;
        this.envProps = envProps;
    }

    @Override
    public ResponseEntity<?> getMeteredCustomerBillPrint(Integer billMonth, Integer billYear) {

        Map<String, Object> reportParams = new HashMap<>();
        try {
            if (utilService.getLoggedInUserCustomerCode().contains("NM-")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bill Print not applicable");
            }
            String customerCode = utilService.getLoggedInUserCustomerCode();
            int customerBillCheck = customerBillRepository.countByCustomerCodeAndBillYearAndBillMonthAndStatus(customerCode, billYear.longValue(), billMonth.longValue(), "Pending");
            if (customerBillCheck > 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bill Print not applicable");
            }
            reportParams.put("zone", utilService.getCustomerZone());
            reportParams.put("bill_month", billMonth);
            reportParams.put("bill_year", billYear);
            reportParams.put("cust_code", customerCode);
            reportParams.put("cust_type_id", utilService.getCustomerType().getId().toString());
            String jasperName = "bill_format_sgcl";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=customer_bill_" + customerCode + "_" + Month.of(billMonth) + "_" + billYear + ".pdf");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .headers(headers)
                    .body(utilService.getPdfReport(jasperName, reportParams, "Customer Bill - " + customerCode));
        } catch (Exception e) {
            logger.error("Exception " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while generating the bill. Please try again later.");
        }
    }

    @Override
    public String getNonMeteredCustomerBill() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String mobileNo = utilService.getLoggedInUserMobileNo();
        String url = envProps.getCOLLECTION_SERVER_URL() + "/nmCustCode/" + customerCode + "/" + mobileNo;
        return HttpUrlConnectionUtil.getContent(url, "GET", envProps.getCOLLECTION_SERVER_BASIC_AUTH());
    }

    @Override
    public Response getNonMeterPaymentInfo() {
        PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String mobileNo = utilService.getLoggedInUserMobileNo();
        paymentInfoDto.setPaymentRef(customerCode.concat(mobileNo));
        String url = envProps.getCOLLECTION_SERVER_URL() + "/nmCustCode/" + customerCode + "/" + mobileNo;

        try {
            String responseContent = HttpUrlConnectionUtil.getContent(url, "GET", envProps.getCOLLECTION_SERVER_BASIC_AUTH());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseContent);

            Optional<JsonNode> paymentData = Optional.ofNullable(jsonNode.path("content").path(0));
            BigDecimal amount = paymentData.map(data ->
                            data.path("Current Total(Surch Incl.)").asText())
                    .map(BigDecimal::new)
                    .orElse(BigDecimal.ZERO);

            paymentInfoDto.setTotalAmount(amount);
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, paymentInfoDto, "Payment Info retrieved successfully");

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing payment info");

        }
    }

    @Override
    public String getMeteredCustomerBill(Boolean isAllBill) {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String mobileNo = utilService.getLoggedInUserMobileNo();
        String url = envProps.getCOLLECTION_SERVER_URL() + "/meteredCustomer/single/" + customerCode + "/" + mobileNo;
        if (isAllBill) {
            url = envProps.getCOLLECTION_SERVER_URL() + "/meteredCustomer/allBill/" + customerCode + "/" + mobileNo;
        }
        return HttpUrlConnectionUtil.getContent(url, "GET", envProps.getCOLLECTION_SERVER_BASIC_AUTH());
    }

    @Override
    public Response getCustomerBillList(Long year) {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        List<CustomerBill> customerBillList = customerBillRepository.findByCustomerCodeAndBillYearAndStatusIsNotOrderByIdDesc(customerCode, year, "Pending");
        if (customerBillList != null && !customerBillList.isEmpty()) {
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, getCustomerBillDtoList(customerBillList), "Customer Bill List retrieved successfully");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "No Bill Found");
    }

    @Override
    public Response getBankList() {
        List<Bank> bankList = bankRepository.findAllByIsActiveTrueAndUsernameNotNullAndNameNotOrderByFullNameAsc("BuetBank");
        List<BankDto> bankDtoList = new ArrayList<>();
        bankList.forEach(bank -> {
            BankDto dto = new BankDto();
            dto.setFullName(bank.getFullName());
            if (bank.getIsMfsOrGateway() == null || !bank.getIsMfsOrGateway()) {
                dto.setType("Online");
            } else {
                dto.setType("Mobile Financial Service");
            }
            bankDtoList.add(dto);
        });
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, bankDtoList, "Online Bank List");
    }

    private List<CustomerBillDto> getCustomerBillDtoList(List<CustomerBill> customerBills) {
        Boolean isNonMetered = false;
        String customerCode = utilService.getLoggedInUserCustomerCode();
        if (customerCode.contains("NM-")) {
            isNonMetered = true;
        }
        List<String> regularStatusList = new ArrayList<>();
        regularStatusList.add("Paid");
        regularStatusList.add("Pending");
        regularStatusList.add("Partial");
        regularStatusList.add("Unpaid");
        List<CustomerBillDto> customerBillDtos = new ArrayList<>();
        Boolean finalIsNonMetered = isNonMetered;
        customerBills.forEach(customerBill -> {
            try {
                CustomerBillDto dto = new CustomerBillDto();
                dto.setBillAmount(customerBill.getBillAmount());
                dto.setBillMonth(DateUtils.getMonthNameFromNumericValue(Integer.parseInt(String.valueOf(customerBill.getBillMonth()))));
                if (customerBill.getLastDateOfPayment() != null) {
                    dto.setLastDateOfPayment(DateUtils.getStringDate(customerBill.getLastDateOfPayment(), "dd-MMM-yyyy"));
                } else {
                    dto.setLastDateOfPayment("N/A");
                }
                dto.setBillYear(customerBill.getBillYear());
                if (customerBill.getStatus() == null || !regularStatusList.contains(customerBill.getStatus())) {
                    dto.setStatus("Problem in bill. Communicate with respective revenue section.");
                } else {
                    dto.setStatus(customerBill.getStatus());
                }
                BigDecimal realTimeSurcharge = new BigDecimal("0");
                if (!customerBill.getStatus().equals("Paid")) {
                    if (finalIsNonMetered) {
                        realTimeSurcharge = customerBillRepository.getRealTimeSurchargeNonMetered(customerCode, new Date());
                        dto.setApplianceQty(customerBill.getApplianceQty());
                    } else {
                        realTimeSurcharge = customerBillRepository.getRealTimeSurchargeMetered(customerCode, new BigDecimal(String.valueOf(customerBill.getBillYear())), new BigDecimal(String.valueOf(customerBill.getBillMonth())), new Date());
                        dto.setMeterRent(customerBill.getMeterRent());
                    }
                }
                dto.setIsNonMetered(finalIsNonMetered);
                dto.setSurcharge(realTimeSurcharge);
                dto.setCurrentTotal(realTimeSurcharge.add(customerBill.getCurrentTotal()));
                dto.setPreviousSurcharge(customerBill.getSurchargeAmount());
                customerBillDtos.add(dto);
            } catch (InvalidMonthException e) {
                logger.error(e.getMessage());
            }
        });
        return customerBillDtos;
    }

    @Override
    public List<String> getYearList() {
        Date currentDate = new Date();
        List<String> yearList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        Integer currentYear = calendar.get(Calendar.YEAR);
        for (int i = currentYear; i > 2000; i--) {
            yearList.add(String.valueOf(i));
        }
        return yearList;
    }

    @Override
    public Response getCustomerUnpaidBillList() {
        List<CustomerBill> unpaidBillList = customerBillRepository.findByCustomerCodeAndStatusOrderByIdDesc(utilService.getLoggedInUserCustomerCode(), "Unpaid");
        if (unpaidBillList != null && unpaidBillList.size() > 0) {
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, getCustomerBillDtoList(unpaidBillList), "Unpaid bill list");
        }
        return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "No unpaid bill found");
    }

    @Override
    public String getPartialMeteredCustomerBill() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String mobileNo = utilService.getLoggedInUserMobileNo();
        String url = envProps.getCOLLECTION_SERVER_URL() + "/meteredPartial/single/" + customerCode + "/" + mobileNo;
        return HttpUrlConnectionUtil.getContent(url, "GET", envProps.getCOLLECTION_SERVER_BASIC_AUTH());
    }

    @Override
    public Response getPartialPaymentDetails() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        List<PartialPaymentDetail> partialPaymentDetails
                = partialPaymentDetailRepository.findByCustCodeAndStatus(customerCode, "Unpaid");
        if (partialPaymentDetails.isEmpty()) {
            return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "No partial payment found");
        }

        List<PartialPaymentDetailsDto> result = partialPaymentDetails.stream().map(
                        partialPaymentDetail -> modelMapper.map(partialPaymentDetail, PartialPaymentDetailsDto.class))
                .collect(Collectors.toList());

        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, result, "Partial payment details");
    }

    public Response getCustomerBillHistoryList(YearMonth start, YearMonth end) {
        // bills before Jan-2009 aren't available in KGDCL
        YearMonth min = YearMonth.of(2009, 1);
        YearMonth max = YearMonth.now();

        // Ensure start and end are within the available range
        start = start.isBefore(min) ? min : (start.isAfter(max) ? max : start);
        end = end.isBefore(min) ? min : (end.isAfter(max) ? max : end);

        // Ensure start is before end
        if (start.isAfter(end)) {
            YearMonth temp = start;
            start = end;
            end = temp;
        }

        String customerCode = utilService.getLoggedInUserCustomerCode();
        List<CustomerBill> customerBillList = customerBillRepository.findCustomerBillBetweenDates(
                customerCode,
                (long) start.getYear(),
                (long) start.getMonthValue(),
                (long) end.getYear(),
                (long) end.getMonthValue()
        );

        return !customerBillList.isEmpty()
                ? ResponseBuilder.getSuccessResponse(HttpStatus.OK, getCustomerBillDtoList(customerBillList), "Customer Bill List retrieved successfully")
                : ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "No bill found in the specified range of dates");
    }

    @Override
    public ResponseEntity<?> getBillCollectionReport(Date startDate, Date endDate) {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        String url = envProps.getERP_SERVER_URL().trim() + "/reportsRev/collectionReportApi";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", envProps.getERP_SERVER_BASIC_AUTH());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        if (startDate.after(endDate)) {
            Date temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("custCode", customerCode)
                .queryParam("fromDate", DateUtils.getStringDate(startDate, "dd-MM-yyyy"))
                .queryParam("toDate", DateUtils.getStringDate(endDate, "dd-MM-yyyy"));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                byte[].class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            byte[] pdfContent = responseEntity.getBody();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_PDF);
            responseHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename("single_customer_collection.pdf").build());

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(pdfContent);
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).body("Error occurred while fetching report");
        }
    }

    @Override
    public boolean isDueBillExist() {
        List<CustomerBill> unPaidBillList = customerBillRepository
                .findByCustomerCodeAndStatusNotOrderByIdDesc(
                        utilService.getLoggedInUserCustomerCode(), "Paid"
                );

        return unPaidBillList != null && !unPaidBillList.isEmpty();
    }

    @Override
    public boolean isBlockBillExist() {
        List<String> excludedStatuses = Arrays.asList("Paid", "Unpaid", "Partial", "FRACTION", "Pending", "AR-SURCHARGE");

        List<CustomerBill> blockBillList = customerBillRepository.findByCustomerCodeAndStatusNotInIgnoreCaseOrderByIdDesc(utilService.getLoggedInUserCustomerCode(), excludedStatuses);

        return blockBillList != null && !blockBillList.isEmpty();
    }

}