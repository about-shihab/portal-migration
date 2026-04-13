package com.iict.buet.customer_portal.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Verification_Token")
@Data
public class VerificationToken extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ParamGenerator")
    @SequenceGenerator(name = "ParamGenerator", sequenceName = "Verification_Token_SEQ", allocationSize = 1)
//    private BigInteger id;
    private Long id;
	private String token;
    @Column(name = "customer_code")
    private String customerCode;
	@Column(name = "expiry_Date")
    private Date expiryDate;
}
