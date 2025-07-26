package com.codewithmosh.arypto.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AirtimePurchaseRequest {
    @SerializedName("request_id")
    private String requestId;

    @SerializedName("serviceID")
    private String serviceID; // "airtel-data"

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("phone")
    private String phone;
}
