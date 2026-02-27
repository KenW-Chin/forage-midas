package com.jpmc.midascore.service;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.jpmc.midascore.foundation.Transaction;

@Service
public class TransactionListener {
    @KafkaListener(topics = "${general.kafka-topic}")
    public void getMessage (Transaction obj){
        System.out.printf("Transaction {senderId=%d, recipientId=%d, amount=%f}", obj.getSenderId(), obj.getRecipientId(), obj.getAmount());
    }
}
