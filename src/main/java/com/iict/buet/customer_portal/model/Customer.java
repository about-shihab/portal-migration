package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "CUST")
@Data
public class Customer extends BaseCustomer{
    @Column(name = "cust_name")
    private String customerName;
    @Column(name = "PHONE_NO")
    private String mobileNo;
}
