package com.podsho.parabank.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Account {
    private int id;
    private int customerId;
    private String type;
    private BigDecimal balance;
}
