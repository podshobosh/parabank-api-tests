package com.podsho.parabank.db;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Transaction {
    int type;
    BigDecimal amount;
    String description;
}
