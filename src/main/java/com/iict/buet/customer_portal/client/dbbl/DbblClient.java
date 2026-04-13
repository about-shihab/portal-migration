package com.iict.buet.customer_portal.client.dbbl;

import com.iict.buet.customer_portal.client.dbbl.dto.GetResultFieldDto;
import com.iict.buet.customer_portal.client.dbbl.dto.GetransidDto;
import com.iict.buet.customer_portal.dto.Response;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface DbblClient {
    Response getGetransidResponse(GetransidDto getransidDto, HttpServletRequest request);
    Response getResultFieldResponse(GetResultFieldDto getResultFieldDto, HttpServletRequest request);
    BigDecimal calculateBankFee(String bankName, String cardType, BigDecimal billAmount);
}
