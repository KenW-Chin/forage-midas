package com.jpmc.midascore;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;

@Service
public class TransactionListener {

    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService){
        this.transactionService = transactionService;
    }
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "transaction-consumers")
    public void listen (Transaction transaction){
        System.out.println("Listener triggered.");
        Transaction t = transaction;
        transactionService.processTransaction(t);
    }
}
