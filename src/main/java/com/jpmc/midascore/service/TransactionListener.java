package com.jpmc.midascore.service;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.jpmc.midascore.foundation.Transaction;

@Service
public class TransactionListener {
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "transaction-consumers")
    public void listen (Transaction transaction){
        System.out.println(transaction);
    }
}
