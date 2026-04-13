package com.iict.buet.customer_portal.client.dbbl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iict.buet.customer_portal.client.dbbl.dto.GetResultFieldDto;
import com.iict.buet.customer_portal.client.dbbl.dto.GetransidDto;
import com.iict.buet.customer_portal.config.EnvironmentProperties;
import com.iict.buet.customer_portal.dbbl_gateway.Getransid;
import com.iict.buet.customer_portal.dbbl_gateway.GetransidResponse;
import com.iict.buet.customer_portal.dbbl_gateway.Getresultfield;
import com.iict.buet.customer_portal.dbbl_gateway.GetresultfieldResponse;
import com.iict.buet.customer_portal.dto.NmApiResponseDto;
import com.iict.buet.customer_portal.dto.PostResponseDto;
import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.model.Bank;
import com.iict.buet.customer_portal.model.GatewayResponse;
import com.iict.buet.customer_portal.repository.BankRepository;
import com.iict.buet.customer_portal.repository.GatewayResponseRepository;
import com.iict.buet.customer_portal.service.BillInfoService;
import com.iict.buet.customer_portal.service.UtilService;
import com.iict.buet.customer_portal.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DbblClientImpl implements DbblClient {
    private static final Logger logger = LogManager.getLogger(DbblClientImpl.class.getName());
    private final WebServiceTemplate webServiceTemplate;
    private final GatewayResponseRepository gatewayResponseRepository;
    private final UtilService utilService;
    private final BillInfoService billInfoService;
    private final BankRepository bankRepository;
    private final IpService ipService;
    private final EnvironmentProperties envProps;
    private final AuthUtils authUtils;

    private BigDecimal getBillAmount() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (utilService.isNonMeteredCustomer()) {
                String apiResponse = billInfoService.getNonMeteredCustomerBill();
                NmApiResponseDto nmApiResponseDto = mapper.readValue(apiResponse, NmApiResponseDto.class);
                if (nmApiResponseDto.getContent() != null && nmApiResponseDto.getContent().size() > 0) {
                    return nmApiResponseDto.getContent().get(0).getCurrentTotalSurchIncl();
                }
            }
            NmApiResponseDto meteredApiResponseDto = getMeterResponse();
            if (meteredApiResponseDto == null) {
                return null;
            }
            return new BigDecimal(meteredApiResponseDto.getContent().get(0).getCurrentTotalSurchIncl().toString());
        } catch (Exception e) {
            return null;
        }
    }

    private NmApiResponseDto getMeterResponse() {
        try {
            String apiResponse = billInfoService.getNonMeteredCustomerBill();
            ObjectMapper mapper = new ObjectMapper();
            NmApiResponseDto meteredApiResponseDto = mapper.readValue(apiResponse, NmApiResponseDto.class);
            if (meteredApiResponseDto.getContent() != null && meteredApiResponseDto.getContent().size() > 0) {
                return meteredApiResponseDto;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public Response getGetransidResponse(GetransidDto getransidDto, HttpServletRequest request) {
        try {
            Response res = authUtils.denyAdminAccess("Admin users are not allowed to pay customer's bill");
            if (res != null) {
                return res;
            }
            if (!utilService.isNonMeteredCustomer()) {
                return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "Internet Gateway payment is not supported for metered customer");
            }
            BigDecimal billAmount = getBillAmount();
            ObjectMapper mapper = new ObjectMapper();
            if (billAmount == null || billAmount.compareTo(new BigDecimal("0")) == 0) {
                return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "Error from api server");
            }
            String customerIp = ipService.getClientIpAddress(request);
            BigDecimal totalAmount = billAmount.add(calculateBankFee(GatewayConstraint.DBBL_NEXUS.name(), getransidDto.getCardtype().toString(), billAmount));
            totalAmount = totalAmount.multiply(new BigDecimal("100"));
            String[] totalAmountString = totalAmount.toString().split("\\.");
            BigInteger payableTotal = new BigInteger(totalAmountString[0]);
            if (totalAmountString.length == 2) {
                if (Integer.parseInt(totalAmountString[1]) > 1) {
                    payableTotal = payableTotal.add(new BigInteger("1"));
                }
            }
            Getransid getransid = new Getransid();
            getransid.setUserid(envProps.getDBBL_SOAP_USERNAME());
            getransid.setPwd(envProps.getDBBL_SOAP_PASSWORD());
            getransid.setClientip(customerIp);
            getransid.setAmount(payableTotal.toString());
            getransid.setCardtype(getransidDto.getCardtype().toString());
            getransid.setTxnrefnum(utilService.getLoggedInUserCustomerCode().concat(utilService.getLoggedInUserMobileNo()));
            String requestJson = mapper.writeValueAsString(getransid);
            GetransidResponse getransidResponse = (GetransidResponse) ((JAXBElement) webServiceTemplate.marshalSendAndReceive(envProps.getDBBL_SOAP_URL(), getransid)).getValue();
            GatewayResponse gatewayResponse = new GatewayResponse();
            gatewayResponse.setCustomerCode(utilService.getLoggedInUserCustomerCode());
            gatewayResponse.setCustomerIp(customerIp);
            gatewayResponse.setIsSuccess(false);
            gatewayResponse.setGatewayName(GatewayConstraint.DBBL_NEXUS.name());
            gatewayResponse.setResponse(getransidResponse.getReturn());
            gatewayResponse.setRequest(requestJson);
            gatewayResponse.setIsUsed(false);
            if (getransidResponse.getReturn().startsWith("TRANSACTION_ID:")) {
                String transactionId = getransidResponse.getReturn().replace("TRANSACTION_ID:", "").trim();
                if (transactionId.length() != 28) {
                    gatewayResponse.setIsSuccess(false);
                    gatewayResponse.setResponse(getransidResponse.getReturn());
                } else {
                    gatewayResponse.setIsSuccess(true);
                    gatewayResponse.setResponse(transactionId);
                    gatewayResponse = gatewayResponseRepository.save(gatewayResponse);
                }
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("transactionId", gatewayResponse.getResponse());
                responseMap.put("cardType", getransidDto.getCardtype());
                responseMap.put("gatewayUrl", envProps.getDBBL_GATEWAY_CLIENT_URL());
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, responseMap, "DBBL Client Response");
            }
            gatewayResponse = gatewayResponseRepository.save(gatewayResponse);
            return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, getransidResponse.getReturn());

        } catch (Exception e) {
            logger.error("Error in getGetransidResponse: " + e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs");
        }
    }

    @Override
    public Response getResultFieldResponse(GetResultFieldDto getResultFieldDto, HttpServletRequest request) {
        try {
            if (!utilService.isNonMeteredCustomer()) {
                return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "Internet Gateway payment is not supported for metered customer");
            }
            String customerCode = utilService.getLoggedInUserCustomerCode();
            String mobileNo = utilService.getLoggedInUserMobileNo();
            BigDecimal billAmount = getBillAmount();
            NmApiResponseDto meteredApiResponseDto = getMeterResponse();
            if (billAmount == null || billAmount.compareTo(new BigDecimal("0")) == 0) {
                return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Customer has no dues");
            }
            if (meteredApiResponseDto == null) {
                return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Invalid Request");
            }
            String customerIp = ipService.getClientIpAddress(request);
            GatewayResponse gatewayResponseOld = gatewayResponseRepository.findByResponseAndCustomerCodeAndGatewayNameAndIsSuccessTrueAndIsUsedFalse(getResultFieldDto.getTransid(), customerCode, GatewayConstraint.DBBL_NEXUS.name());
            if (gatewayResponseOld == null) {
                return ResponseBuilder.getFailResponse(HttpStatus.BAD_REQUEST, "Invalid transaction ID");
            }
            ObjectMapper mapper = new ObjectMapper();
            GatewayResponse gatewayResponse = new GatewayResponse();
            Getresultfield getresultfield = new Getresultfield();
            getresultfield.setBillinginfo(customerCode.concat(mobileNo));
            getresultfield.setClientip(customerIp);
            getresultfield.setUserid(envProps.getDBBL_SOAP_USERNAME());
            getresultfield.setPwd(envProps.getDBBL_SOAP_PASSWORD());
            getresultfield.setTransid(gatewayResponseOld.getResponse());
            String requestJson = mapper.writeValueAsString(getresultfield);
            gatewayResponse.setIsUsed(null);
            gatewayResponse.setRequest(requestJson);
            GetresultfieldResponse getresultfieldResponse = (GetresultfieldResponse) ((JAXBElement) webServiceTemplate.marshalSendAndReceive(envProps.getDBBL_SOAP_URL(), getresultfield)).getValue();
            String[] results = getresultfieldResponse.getReturn().split("\\^");
            gatewayResponse.setIsSuccess(null);
            gatewayResponse.setResponse(getresultfieldResponse.getReturn());
            gatewayResponse.setCustomerCode(customerCode);
            gatewayResponse.setGatewayName(GatewayConstraint.DBBL_NEXUS.name());
            gatewayResponse.setCustomerIp(customerIp);
            Map<String, String> resultMap = new HashMap<>();
            for (int i = 0; i < results.length; i++) {
                String[] keyValue = results[i].split(">");
                if (keyValue.length == 2) {
                    resultMap.put(keyValue[0], keyValue[1]);
                }
            }

            logger.info("Result Map: " + resultMap);

            gatewayResponseOld.setIsUsed(true);
            gatewayResponseOld = gatewayResponseRepository.save(gatewayResponseOld);
            if (resultMap.containsKey("RESULT") && resultMap.get("RESULT").equals("OK") && resultMap.containsKey("RESULT_CODE") && resultMap.get("RESULT_CODE").equals("000")) {
                String subUrl = "/nmBillPay/" + GatewayConstraint.DBBL_NEXUS.name() + "/KGDCL_PORTAL/" + customerCode + "/" + mobileNo + "/" + billAmount + "";
                String url = envProps.getCOLLECTION_SERVER_URL() + subUrl;
                String response = HttpUrlConnectionUtil.getContent(url, "POST", envProps.getDBBL_COLLECTION_BASIC_AUTH());
                PostResponseDto postResponseDto = mapper.readValue(response, PostResponseDto.class);
                String kgdclTransactionRef = postResponseDto.getContent().getTxId();
                resultMap.put("kgdclTransactionRef", kgdclTransactionRef);
                resultMap.put("AMOUNT", "Tk. " + (Integer.parseInt(resultMap.get("AMOUNT")) / 100));
                gatewayResponse.setKgdclTraRef(kgdclTransactionRef);
                gatewayResponse.setRrn(resultMap.get("RRN"));
                gatewayResponse = gatewayResponseRepository.save(gatewayResponse);
                return ResponseBuilder.getSuccessResponse(HttpStatus.OK, resultMap, "Payment Successful");
            }
            gatewayResponse = gatewayResponseRepository.save(gatewayResponse);
            if (resultMap.containsKey("RESULT")) {
                if (resultMap.get("RESULT_CODE").equals("116")) {
                    return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "Insufficient Balance");
                } else if (resultMap.get("RESULT_CODE").equals("117")) {
                    return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "Incorrect PIN");
                }
            }

            return ResponseBuilder.getFailResponse(HttpStatus.NOT_ACCEPTABLE, "Payment failed. Consult with your bank and try again");
        } catch (Exception e) {
            logger.error("Error in getResultFieldResponse: " + e.getMessage());
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error Occurs");
        }
    }

    @Override
    public BigDecimal calculateBankFee(String bankName, String cardType, BigDecimal billAmount) {
        Bank bank = bankRepository.findByNameAndIsActiveTrue(bankName);
        if (bank == null) {
            return new BigDecimal("0");
        }
        if (cardType.equals("1")) {
            return bank.getOwnCardCost();
        }
        if (cardType.equals("2") || cardType.equals("3") || cardType.equals("4") || cardType.equals("5")) {
            return billAmount.multiply(bank.getOtherCardCost());
        }
        return billAmount.multiply(bank.getDbblRocketCost());
    }

    @PostConstruct
    public void disableSSL() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

}
