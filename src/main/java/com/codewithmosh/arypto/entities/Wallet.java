package com.codewithmosh.arypto.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "deposit_address")
    private String depositAddress;

    @Column(name = "crypto_currency")
    private String cryptoCurrency;

    @Column(name = "network")
    private String network;

//    @Column(name = "is_active")
//    private boolean isActive;

    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "wallet")
    @Builder.Default
    private Set<Transaction> transactions = new LinkedHashSet<>();
}
