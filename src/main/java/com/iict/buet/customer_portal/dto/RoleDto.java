package com.iict.buet.customer_portal.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class RoleDto {

    @JsonIgnore
    private Long id;
    @JsonIgnore
    private List<UserDto> users;
    @JsonIgnore
    private Long priority;
    @JsonIgnore
    private String description;
    private String name;
}
