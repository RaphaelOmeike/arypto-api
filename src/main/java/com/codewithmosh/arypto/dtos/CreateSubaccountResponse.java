package com.codewithmosh.arypto.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CreateSubaccountResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private SubaccountData data;

    @Data
    public static class SubaccountData {
        @SerializedName("id")
        private String id;

        @SerializedName("sn")
        private String sn;

        @SerializedName("email")
        private String email;

        @SerializedName("first_name")
        private String firstName;

        @SerializedName("last_name")
        private String lastName;
    }
}

