package com.iict.buet.customer_portal.repository;

import com.iict.buet.customer_portal.model.CustomerBill;
import com.iict.buet.customer_portal.util.QueryConstraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CustomerBillRepository extends JpaRepository<CustomerBill, Long> {
    List<CustomerBill> findByCustomerCodeAndStatusNotOrderByIdDesc(String customerCode, String status);

    List<CustomerBill> findByCustomerCodeAndStatusNotInIgnoreCaseOrderByIdDesc(String customerCode, List<String> statuses);

    List<CustomerBill> findByCustomerCodeAndBillYearAndStatusIsNotOrderByIdDesc(String customerCode, Long billYear, String status);

    int countByCustomerCodeAndBillYearAndBillMonthAndStatus(String customerCode, Long billYear, Long billMonth, String status);

    List<CustomerBill> findByCustomerCodeAndStatusOrderByIdDesc(String customerCode, String status);

    @Query("SELECT c FROM CUSTOMER_BILL c " +
            "WHERE c.customerCode = :customerCode " +
            "AND (c.billYear = :fromYear AND c.billMonth >= :fromMonth OR c.billYear > :fromYear) " +
            "AND (c.billYear = :toYear AND c.billMonth <= :toMonth OR c.billYear < :toYear) " +
            "ORDER BY c.id desc")
    List<CustomerBill> findCustomerBillBetweenDates(
            @Param("customerCode") @NonNull String customerCode,
            @Param("fromYear") @NonNull Long fromYear,
            @Param("fromMonth") @NonNull Long fromMonth,
            @Param("toYear") @NonNull Long toYear,
            @Param("toMonth") @NonNull Long toMonth
    );


    @Query(value = QueryConstraint.CustomerBillManagement.REAL_TIME_SURCHARGE_METERED_FUNC, nativeQuery = true)
    BigDecimal getRealTimeSurchargeMetered(@Param("customerCode") String code, @Param("billYear") BigDecimal billYear, @Param("billMonth") BigDecimal billMonth, @Param("chequePayDate") Date chequePayDate);

    @Query(value = QueryConstraint.CustomerBillManagement.REAL_TIME_SURCHARGE_NON_METERED_FUNC, nativeQuery = true)
    BigDecimal getRealTimeSurchargeNonMetered(@Param("customerCode") String code, @Param("payDate") Date payDate);

    @Query(value = QueryConstraint.CustomerBillManagement.CERTIFICATE_DETERMINER, nativeQuery = true)
    int getCustomerCertificateTypeMetered(@Param("inCustCode") String custCode,
                                          @Param("inBillYear") Long billYear,
                                          @Param("inBillMonth") Long billMonth);

    @Query(value = QueryConstraint.CustomerBillManagement.CERTIFICATE_DETERMINER_NM, nativeQuery = true)
    int getCustomerCertificateTypeNonMetered(@Param("inCustCode") String custCode);

    @Query(value = QueryConstraint.CustomerBillManagement.CERTIFICATE_DETERMINER_ALL, nativeQuery = true)
    int getCustomerCertificateDeterminer(@Param("inCustCode") String custCode,
                                         @Param("inBillYear") Long billYear,
                                         @Param("inBillMonth") Long billMonth);
}
