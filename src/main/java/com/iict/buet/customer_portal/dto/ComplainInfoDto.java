package com.iict.buet.customer_portal.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ComplainInfoDto {
    @NotNull(message = "Complain Cause required")
    private Long causeId;
    @NotBlank(message = "Complain description required")
    private String description;
}
