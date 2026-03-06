package com.jpmc.midascore.service;

import org.springframework.stereotype.Service;
import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;

@Service
public class TransactionService{

    private final UserRepository userRepository;
    private final DatabaseConduit databaseConduit;

    public TransactionService(UserRepository userRepository, DatabaseConduit databaseConduit){
        this.userRepository = userRepository;
        this.databaseConduit = databaseConduit;
    }

    public void processTransaction(Transaction kafkaTransaction){
        if(!userRepository.existsById(kafkaTransaction.getSenderId()) || !userRepository.existsById(kafkaTransaction.getRecipientId())){
            // decline
            System.out.println("Invalid Sender or Recipient ID. Please try again.");
        }
        else{
            UserRecord senderRec = userRepository.findById(kafkaTransaction.getSenderId());
            UserRecord recepRec = userRepository.findById(kafkaTransaction.getRecipientId());
            if(kafkaTransaction.getAmount() > senderRec.getBalance()){
                // decline
                System.out.println("Amount of money sent exceeds current amount. Please try again.");
            }
            else{
                recepRec.setBalance(recepRec.getBalance() + kafkaTransaction.getAmount());
                senderRec.setBalance(senderRec.getBalance() - kafkaTransaction.getAmount());
                // save recep
                databaseConduit.save(recepRec);
                // save sender
                databaseConduit.save(senderRec);
                // create and save transactionRecord
                new TransactionRecord(senderRec, recepRec, kafkaTransaction.getAmount());
            }

        }
    }
}
