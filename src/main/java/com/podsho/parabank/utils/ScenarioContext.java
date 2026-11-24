package com.podsho.parabank.utils;

import java.math.BigDecimal;

import io.restassured.response.Response;

/**
 * Shared state for a single scenario.
 *
 * Picocontainer creates one instance per scenario and injects the same object
 * into every step-definition class that asks for it in its constructor, so
 * steps in different classes can read and write the same response.
 */
public class ScenarioContext {

    private String accountId;
    private String customerId;
    private BigDecimal balance;
    private BigDecimal amount;

    private String firstAccountId;
    private String secondAccountId;
    private BigDecimal firstBalance;
    private BigDecimal secondBalance;

    public String getFirstAccountId() {
        return firstAccountId;
    }

    public void setFirstAccountId(String firstAccountId) {
        this.firstAccountId = firstAccountId;
    }

    public String getSecondAccountId() {
        return secondAccountId;
    }

    public void setSecondAccountId(String secondAccountId) {
        this.secondAccountId = secondAccountId;
    }

    public BigDecimal getFirstBalance() {
        return firstBalance;
    }

    public void setFirstBalance(BigDecimal firstBalance) {
        this.firstBalance = firstBalance;
    }

    public BigDecimal getSecondBalance() {
        return secondBalance;
    }

    public void setSecondBalance(BigDecimal secondBalance) {
        this.secondBalance = secondBalance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String id) {
        this.accountId = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
