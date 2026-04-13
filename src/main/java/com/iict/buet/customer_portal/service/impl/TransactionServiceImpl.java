package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.dto.TransactionDto;
import com.iict.buet.customer_portal.model.Transaction;
import com.iict.buet.customer_portal.repository.TransactionRepository;
import com.iict.buet.customer_portal.service.TransactionService;
import com.iict.buet.customer_portal.service.UtilService;
import com.iict.buet.customer_portal.util.DateUtils;
import com.iict.buet.customer_portal.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {
    private final UtilService utilService;
    private final TransactionRepository transactionRepository;
    public TransactionServiceImpl(UtilService utilService, TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
        this.utilService = utilService;
    }
    @Override
    public Response getTransactionInfo() {
        String customerCode = utilService.getLoggedInUserCustomerCode();
        List<Transaction> transactionList = transactionRepository.findAllByCustomerCode(customerCode);
        if(transactionList == null && transactionList.size()==0){
            return ResponseBuilder.getFailResponse(HttpStatus.NOT_FOUND, "No Data Found");
        }
        return ResponseBuilder.getSuccessResponse(HttpStatus.OK, getTransactionDtoList(transactionList), "Transaction List Retrieve Successfully.");
    }

    private List<TransactionDto> getTransactionDtoList(List<Transaction> transactionList){
        List<TransactionDto> transactionDtoList = new ArrayList<>();
        transactionList.forEach(transaction -> {
            TransactionDto dto = new TransactionDto();
            dto.setBankName(transaction.getBank().getFullName());
            dto.setBranchName(transaction.getBranchName());
            dto.setTransactionDate(DateUtils.getStringDate(transaction.getTransactionDate(), "dd-MMM-yyyy"));
            dto.setTransactionAmount(transaction.getTransactionTotal().toString());
            dto.setTransactionRef(transaction.getTransactionRef());
            dto.setTransactionType(transaction.getTransactionType().getName());
            transactionDtoList.add(dto);
        });
        return transactionDtoList;
    }
}
