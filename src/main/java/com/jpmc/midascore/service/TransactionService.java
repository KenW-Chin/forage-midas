package com.jpmc.midascore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import com.jpmc.midascore.repository.TransactionRepository;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Service
public class TransactionService{

    public static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository){
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void processTransaction(Transaction kafkaTransaction){

        UserRecord sender = userRepository.findById(kafkaTransaction.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(kafkaTransaction.getRecipientId()).orElse(null);
        
        boolean isValid = validateTransaction(kafkaTransaction, sender, recipient);
        
        if(isValid) {
            // Execute the transaction
            executeTransaction(kafkaTransaction, sender, recipient);
            logger.info("Transaction executed successfully: {}", kafkaTransaction);
        } else {
            logger.warn("Transaction validation failed: {}", kafkaTransaction);
        }
        // create and save transactionRecord
        TransactionRecord record = TransactionRecord.fromTransaction(kafkaTransaction, sender, recipient, isValid);
        transactionRepository.save(record);
        logger.info("Transaction recorded: {}", record);
    }

    private boolean validateTransaction(Transaction kafkaTransaction, UserRecord sender, UserRecord recipient){
        if (sender == null) {
            logger.warn("Sender with ID {} not found", kafkaTransaction.getSenderId());
            return false;
        }
        if (recipient == null) {
            logger.warn("Recipient with ID {} not found", kafkaTransaction.getRecipientId());
            return false;
        }

        if (sender.getBalance() < kafkaTransaction.getAmount()) {
            logger.warn("Insufficient balance for sender {}: required {}, available {}",
                sender.getName(), kafkaTransaction.getAmount(), sender.getBalance()
            );
            return false;
        }

        if (kafkaTransaction.getAmount() <= 0) {
            logger.warn("Invalid transaction amount: {}", kafkaTransaction.getAmount());
            return false;
        }

        return true;
    }

    private void executeTransaction(Transaction kafkaTransaction, UserRecord sender, UserRecord recipient){
        float newSenderBalance = sender.getBalance() - kafkaTransaction.getAmount();
        sender.setBalance(newSenderBalance);
        userRepository.save(sender);

        float newRecipientBalance = recipient.getBalance() + kafkaTransaction.getAmount();
        recipient.setBalance(newRecipientBalance);
        userRepository.save(recipient);

        logger.info("Balances updated - Sender {}: {} -> {}, Recipient {}: {} -> {}",
            sender.getName(), sender.getBalance() + kafkaTransaction.getAmount(), newSenderBalance,
            recipient.getName(), recipient.getBalance() - kafkaTransaction.getAmount(), newRecipientBalance
        );
    }
    
}
