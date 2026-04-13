package com.iict.buet.customer_portal.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "NAME",
        "Category",
        "Zone",
        "Current Total(Surch Incl.)",
        "Surcharge Amount",
        "Last Payment Date",
        "Billing Months",
        "Is Govt."
})
public class ContentNm {

    @JsonProperty("NAME")
    private String nAME;
    @JsonProperty("Category")
    private String category;
    @JsonProperty("Zone")
    private String zone;
    @JsonProperty("Current Total(Surch Incl.)")
    private BigDecimal currentTotalSurchIncl;
    @JsonProperty("Surcharge Amount")
    private BigDecimal surchargeAmount;
    @JsonProperty("Last Payment Date")
    private String lastPaymentDate;
    @JsonProperty("Billing Months")
    private String billingMonths;
    @JsonProperty("Is Govt.")
    private Boolean isGovt;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("NAME")
    public String getNAME() {
        return nAME;
    }

    @JsonProperty("NAME")
    public void setNAME(String nAME) {
        this.nAME = nAME;
    }

    @JsonProperty("Category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("Category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("Zone")
    public String getZone() {
        return zone;
    }

    @JsonProperty("Zone")
    public void setZone(String zone) {
        this.zone = zone;
    }

    @JsonProperty("Current Total(Surch Incl.)")
    public BigDecimal getCurrentTotalSurchIncl() {
        return currentTotalSurchIncl;
    }

    @JsonProperty("Current Total(Surch Incl.)")
    public void setCurrentTotalSurchIncl(BigDecimal currentTotalSurchIncl) {
        this.currentTotalSurchIncl = currentTotalSurchIncl;
    }

    @JsonProperty("Surcharge Amount")
    public BigDecimal getSurchargeAmount() {
        return surchargeAmount;
    }

    @JsonProperty("Surcharge Amount")
    public void setSurchargeAmount(BigDecimal surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
    }

    @JsonProperty("Last Payment Date")
    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    @JsonProperty("Last Payment Date")
    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    @JsonProperty("Billing Months")
    public String getBillingMonths() {
        return billingMonths;
    }

    @JsonProperty("Billing Months")
    public void setBillingMonths(String billingMonths) {
        this.billingMonths = billingMonths;
    }

    @JsonProperty("Is Govt.")
    public Boolean getIsGovt() {
        return isGovt;
    }

    @JsonProperty("Is Govt.")
    public void setIsGovt(Boolean isGovt) {
        this.isGovt = isGovt;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}