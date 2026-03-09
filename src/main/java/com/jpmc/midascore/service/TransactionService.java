package com.jpmc.midascore.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Service
public class TransactionService{

    public static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;

    public TransactionService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void processTransaction(Transaction kafkaTransaction){
        if(!userRepository.existsById(kafkaTransaction.getSenderId()) || !userRepository.existsById(kafkaTransaction.getRecipientId())){
            // decline
            logger.info("Invalid Sender or Recipient ID. Please try again.");
            return;
        }
        else{
            UserRecord senderRec = userRepository.findById(kafkaTransaction.getSenderId());
            UserRecord recepRec = userRepository.findById(kafkaTransaction.getRecipientId());
            if(kafkaTransaction.getAmount() > senderRec.getBalance()){
                // decline
                logger.info("Amount of money sent exceeds current amount. Please try again.");
                return;
            }
            else{
                if (senderRec.getName() == "waldorf"){
                    logger.info("Waldorf balance: {}", senderRec.getBalance());
                }
                if (recepRec.getName() == "waldorf"){
                    logger.info("Waldorf balance: {}", recepRec.getBalance());
                }  
                recepRec.setBalance(recepRec.getBalance() + kafkaTransaction.getAmount());
                senderRec.setBalance(senderRec.getBalance() - kafkaTransaction.getAmount());
                // save recep
                userRepository.save(recepRec);
                // save sender
                userRepository.save(senderRec);
                // create and save transactionRecord
            }

        }
    }
}
