package com.codewithmosh.arypto.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TickerResponse {
    @SerializedName("data")
    private TickerDataWrapper data;

    @Data
    public static class TickerDataWrapper {
        @SerializedName("ticker")
        private TickerData ticker;
    }

    @Data
    public static class TickerData {
        @SerializedName("buy")
        private BigDecimal buy;

        @SerializedName("sell")
        private BigDecimal sell;

        @SerializedName("last")
        private BigDecimal last_price;
    }
}
