package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "CUSTOMER_BILL")
@Data
public class CustomerBill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "BILL_MONTH")
    private Long billMonth;
    @Column(name = "BILL_YEAR")
    private Long billYear;
    @Column(name = "CUST_CODE")
    private String customerCode;
    private String status;
    @Column(name = "BILL_AMOUNT")
    private BigDecimal billAmount;
    @Column(name = "CURRENT_TOTAL")
    private BigDecimal currentTotal;
    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_DATE_PAYMENT")
    private Date lastDateOfPayment;
    @Column(name = "METER_CHARGE_AMOUNT")
    private BigDecimal meterRent;
    @Column(name = "SURCHARGE_AMOUNT")
    private BigDecimal surchargeAmount;
    @Column(name = "ACTUAL_APPLIANCE_QTY")
    private BigDecimal applianceQty;

}
