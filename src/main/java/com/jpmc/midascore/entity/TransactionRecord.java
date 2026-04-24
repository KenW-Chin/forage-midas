package com.jpmc.midascore.entity;

import jakarta.persistence.*;

import com.jpmc.midascore.foundation.Transaction;;

@Entity
@Table(name = "transaction_records")
public class TransactionRecord{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private boolean valid; 

    public TransactionRecord(){}

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, boolean valid){
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.valid = valid;
    }

    public static TransactionRecord fromTransaction(Transaction transaction, UserRecord sender, UserRecord recipient, boolean valid) {
        return new TransactionRecord(sender, recipient, transaction.getAmount(), valid);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public UserRecord getSender() { return sender; }
    public void setSender(UserRecord sender) { this.sender = sender; }
    
    public UserRecord getRecipient() { return recipient; }
    public void setRecipient(UserRecord recipient) { this.recipient = recipient; }
    
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    @Override
    public String toString() {
        return String.format("TransactionRecord[id=%d, sender='%s', recipient='%s', amount=%.2f, valid=%s]",
                id, sender != null ? sender.getName() : "null", 
                recipient != null ? recipient.getName() : "null", 
                amount, valid);
    }
}
