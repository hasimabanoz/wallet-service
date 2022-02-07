package com.playtomic.tests.wallet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.playtomic.tests.wallet.entity.Transaction;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    
}
