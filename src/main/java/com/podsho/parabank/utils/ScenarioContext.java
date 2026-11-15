package com.podsho.parabank.utils;

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
    private double balance;
    private double amount;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String id) {
        this.accountId = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

     public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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
