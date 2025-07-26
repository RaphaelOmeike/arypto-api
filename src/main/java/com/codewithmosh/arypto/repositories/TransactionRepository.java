package com.codewithmosh.arypto.repositories;

import com.codewithmosh.arypto.entities.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    Optional<Transaction> findByRequestId(String requestId);
}
