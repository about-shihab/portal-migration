package com.iict.buet.customer_portal.dto;

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
        "Customer Name",
        "Category",
        "Zone",
        "Is Govt.",
        "Bill Month",
        "Bill Year",
        "Gas Bill",
        "Surcharge",
        "Meter Charge",
        "Total Bill Amount",
        "Last Payment Date"
})
public class ContentMetered {

    @JsonProperty("Customer Name")
    private String customerName;
    @JsonProperty("Category")
    private String category;
    @JsonProperty("Zone")
    private String zone;
    @JsonProperty("Is Govt.")
    private Boolean isGovt;
    @JsonProperty("Bill Month")
    private String billMonth;
    @JsonProperty("Bill Year")
    private Integer billYear;
    @JsonProperty("Gas Bill")
    private String gasBill;
    @JsonProperty("Surcharge")
    private String surcharge;
    @JsonProperty("Meter Charge")
    private String meterCharge;
    @JsonProperty("Total Bill Amount")
    private String totalBillAmount;
    @JsonProperty("Last Payment Date")
    private String lastPaymentDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("Customer Name")
    public String getCustomerName() {
        return customerName;
    }

    @JsonProperty("Customer Name")
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    @JsonProperty("Is Govt.")
    public Boolean getIsGovt() {
        return isGovt;
    }

    @JsonProperty("Is Govt.")
    public void setIsGovt(Boolean isGovt) {
        this.isGovt = isGovt;
    }

    @JsonProperty("Bill Month")
    public String getBillMonth() {
        return billMonth;
    }

    @JsonProperty("Bill Month")
    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
    }

    @JsonProperty("Bill Year")
    public Integer getBillYear() {
        return billYear;
    }

    @JsonProperty("Bill Year")
    public void setBillYear(Integer billYear) {
        this.billYear = billYear;
    }

    @JsonProperty("Gas Bill")
    public String getGasBill() {
        return gasBill;
    }

    @JsonProperty("Gas Bill")
    public void setGasBill(String gasBill) {
        this.gasBill = gasBill;
    }

    @JsonProperty("Surcharge")
    public String getSurcharge() {
        return surcharge;
    }

    @JsonProperty("Surcharge")
    public void setSurcharge(String surcharge) {
        this.surcharge = surcharge;
    }

    @JsonProperty("Meter Charge")
    public String getMeterCharge() {
        return meterCharge;
    }

    @JsonProperty("Meter Charge")
    public void setMeterCharge(String meterCharge) {
        this.meterCharge = meterCharge;
    }

    @JsonProperty("Total Bill Amount")
    public String getTotalBillAmount() {
        return totalBillAmount;
    }

    @JsonProperty("Total Bill Amount")
    public void setTotalBillAmount(String totalBillAmount) {
        this.totalBillAmount = totalBillAmount;
    }

    @JsonProperty("Last Payment Date")
    public String getLastPaymentDate() {
        return lastPaymentDate;
    }

    @JsonProperty("Last Payment Date")
    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
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