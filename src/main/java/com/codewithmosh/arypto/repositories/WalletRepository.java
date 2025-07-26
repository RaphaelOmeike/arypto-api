package com.codewithmosh.arypto.repositories;

import com.codewithmosh.arypto.entities.Wallet;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, String> {
}
