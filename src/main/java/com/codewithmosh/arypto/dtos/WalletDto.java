package com.codewithmosh.arypto.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WalletDto {
    private String id;

    private String depositAddress;

    private String cryptoCurrency;

    private String network;

    private BigDecimal balance;
    private List<TransactionDto> transactions;
    private String userId;
}
