package com.iict.buet.customer_portal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by Mozahid on 5/25/17.
 */
@Data
@Builder
@AllArgsConstructor
public class LoginResponseDto {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String token;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String userType;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long userId;
    private List<RoleDto> roles;

}
