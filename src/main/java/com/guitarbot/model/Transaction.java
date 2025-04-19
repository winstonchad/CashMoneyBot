package com.guitarbot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table (name="transactions")

public class Transaction {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String description;
    private String category;

    private LocalDateTime timestamp;

    public Transaction(){
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(Double amount, String description, String category){
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId(){
        return id;
    }

    public Double getAmount(){
        return amount;
    }

    public void setAmount(Double amount){
        this.amount = amount;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public LocalDateTime getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }
}
