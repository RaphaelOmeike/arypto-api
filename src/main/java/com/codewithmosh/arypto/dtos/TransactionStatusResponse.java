package com.codewithmosh.arypto.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionStatusResponse {
    @SerializedName("requestId")
    private String requestId;

    @SerializedName("content")
    private Content content;

    @Data
    public static class Content {
        @SerializedName("transactions")
        private Transaction transaction;
    }

    @Data
    public static class Transaction {
        @SerializedName("status")
        private String status;
    }
}
