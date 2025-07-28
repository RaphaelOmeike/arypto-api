package com.codewithmosh.arypto.dtos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class FetchWalletsResponse {
    @SerializedName("data")
    private List<FetchWalletResponse> wallets;
}
