package com.codewithmosh.arypto.repositories;

import com.codewithmosh.arypto.entities.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}
