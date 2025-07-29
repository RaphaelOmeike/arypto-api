package com.codewithmosh.arypto.repositories;

import com.codewithmosh.arypto.entities.Wallet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    @EntityGraph(attributePaths = "transactions")
    @Query("SELECT w FROM Wallet w WHERE w.id = :walletId")
    Optional<Wallet> getWalletWithTransactions(String walletId);
}
