package com.podsho.parabank.client;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.Response;

public class AccountService {

    public double getBalance(String accountId) {
        return ApiClient.get("/accounts/" + accountId).jsonPath().getDouble("balance");
    }

    public Response deposit(String accountId, double amount) {

        Map<String, Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("amount", amount);
        return ApiClient.postWithParams("/deposit", params);
    }
}
