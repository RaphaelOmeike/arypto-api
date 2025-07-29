package com.codewithmosh.arypto.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Data
public class BuyAirtimeRequest {

    private String network; // "airtel-data"

    private BigDecimal amount;

    private String phone;

    private String walletId;
}
