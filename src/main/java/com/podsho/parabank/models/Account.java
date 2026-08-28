package com.podsho.parabank.models;

import lombok.Data;

@Data
public class Account {
    private int id;
    private int customerId;
    private String type;
    private double balance;
}
