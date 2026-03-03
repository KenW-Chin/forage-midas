package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer{

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    @Value("${general.kafka-topic}")
    private String topic;

    private KafkaProducer(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate; 
    }

    public void send(String message) {
        String[] parts = message.split(",");

        long senderId = Long.parseLong(parts[0].trim());
        long recipientId = Long.parseLong(parts[1].trim());
        float amount = Float.parseFloat(parts[2].trim());

        Transaction transaction = new Transaction(senderId, recipientId, amount);

        kafkaTemplate.send(topic, transaction);
    }
}