package com.jpmc.midascore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListener;

@SpringBootApplication
public class MidasCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MidasCoreApplication.class, args);
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    public void TransactionListener (Transaction obj){
        System.out.printf("Transaction {senderId=%d, recipientId=%d, amount=%f}", obj.senderId, obj.recipientId, obj.amount);
    }
}

