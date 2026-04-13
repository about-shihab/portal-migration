package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "CUST_DOMESTIC")
@Data
public class CustomerDomestic extends BaseCustomer{
    @Column(name = "CUSTOMER_NAME")
    private String customerName;
    @Column(name = "MOBILE_NO")
    private String mobileNo;
}
