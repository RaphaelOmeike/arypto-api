package com.codewithmosh.arypto.dtos;

import com.codewithmosh.arypto.entities.DeliveryStatus;
import com.codewithmosh.arypto.entities.TransactionStatus;
import com.codewithmosh.arypto.entities.TransactionType;
import com.codewithmosh.arypto.entities.Wallet;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long id;

    private String phoneNumber;

    private String serviceId;

    private BigDecimal amountNaira;

    private BigDecimal amountCrypto;

    private String cryptoCurrency;

    private WalletDto wallet;

    private TransactionType transactionType;

    private TransactionStatus transactionStatus;

    private DeliveryStatus deliveryStatus;

    private String requestId;

    private String transactionHash;

    private String transactionId;

    private LocalDateTime createdAt;
}
