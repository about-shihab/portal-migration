package com.iict.buet.customer_portal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "CUSTOMER_PORTAL_ROLE")
@Data
public class Role extends  BaseModel {

    @Id
    @GeneratedValue(strategy =  GenerationType.SEQUENCE, generator = "ParamGenerator")
    @SequenceGenerator(name = "ParamGenerator", sequenceName = "CUSTOMER_PORTAL_ROLE_SEQ", allocationSize = 1)
//    private BigInteger id;
    private Long id;
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List< User> users = new ArrayList<>();
    @Column(name = "role_name")
    private String name;
}