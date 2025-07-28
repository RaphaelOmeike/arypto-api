package com.codewithmosh.arypto.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePaymentAddressRequest {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("currency")
    private String cryptoCurrency;
}
