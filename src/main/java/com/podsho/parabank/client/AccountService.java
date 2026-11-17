package com.podsho.parabank.client;

public class AccountService {
    
    public double getBalance(String accountId){
        return ApiClient.get("/accounts/" + accountId).jsonPath().getDouble("balance");
    }
}
