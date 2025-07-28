package com.codewithmosh.arypto.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "amount_naira")
    private BigDecimal amountNaira;

    @Column(name = "amount_crypto")
    private BigDecimal amountCrypto;

    @Column(name = "crypto_currency")
    private String cryptoCurrency;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "transaction_status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Column(name = "delivery_status")
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "transaction_hash")
    private String transactionHash;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", updatable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_terminated")
    private Boolean isTerminated;

}
