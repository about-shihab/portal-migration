package com.iict.buet.customer_portal.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@Immutable
@Entity
@Table(name = "PARTIAL_PAYMENT_DETAILS")
public class PartialPaymentDetail {
    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "PARTIAL_AMOUNT")
    private Long partialAmount;

    @Column(name = "INSTALLMENT_NO")
    private Long installmentNo;

    @Size(max = 50)
    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "BILL_MONTH")
    private Long billMonth;

    @Column(name = "BILL_YEAR")
    private Long billYear;

    @Column(name = "LAST_DATE_OF_PAYMENT")
    private LocalDate lastDateOfPayment;

    @Column(name = "NUMBER_OF_MONTH")
    private Long numberOfMonth;

    @Size(max = 100)
    @Column(name = "CUST_CODE", length = 100)
    private String custCode;
}
