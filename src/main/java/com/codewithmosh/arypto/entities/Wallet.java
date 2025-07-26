package com.codewithmosh.arypto.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

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

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "wallet")
    private Set<Transaction> transactions = new LinkedHashSet<>();
}
