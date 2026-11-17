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

    public String login(String username, String password) {

        Response loginResponse = ApiClient.get("/login/" + username + "/" + password);

        if (loginResponse.getStatusCode() != 200) {
            throw new IllegalStateException(
                    "Login: Expect status code is 200 but got " + loginResponse.getStatusCode() + ": "
                            + loginResponse.asString());
        }

        String customerId = loginResponse.jsonPath().getString("id");
        return customerId;
    }

    public String getFirstAccountId(String customerId) {

        Response accountsResponse = ApiClient.get("/customers/" + customerId + "/accounts");
        if (accountsResponse.getStatusCode() != 200) {
            throw new IllegalStateException(
                    "Listing accounts: Expect status code is 200 but got " + accountsResponse.getStatusCode() + ": "
                            + accountsResponse.asString());
        }

        return accountsResponse.jsonPath().getString("[0].id");

    }

    public Response createAccount(String customerId, String accountType, String fromAccountId) {

        int typeCode = accountType.equalsIgnoreCase("SAVINGS") ? 1 : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        params.put("newAccountType", typeCode);
        params.put("fromAccountId", fromAccountId);

        return ApiClient.postWithParams("/createAccount", params);

    }

}
