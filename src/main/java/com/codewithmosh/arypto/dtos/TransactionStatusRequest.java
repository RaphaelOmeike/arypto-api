package com.codewithmosh.arypto.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionStatusRequest {
    @SerializedName("request_id")
    private String requestId;
}
