package com.iict.buet.customer_portal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DBBLResponseDto {

    private String trans_id;
    private String Ucaf_Cardholder_Confirm;
    private int card_type;

    @Override
    public String toString() {
        return "DBBLResponseDto{" +
                "trans_id='" + trans_id + '\'' +
                ", Ucaf_Cardholder_Confirm='" + Ucaf_Cardholder_Confirm + '\'' +
                ", card_type=" + card_type +
                '}';
    }
}
