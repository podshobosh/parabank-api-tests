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

        String customerId = loginResponse.jsonPath().getString("id");
        requireOk(loginResponse, "Login");
        
        return customerId;
    }

    public String getFirstAccountId(String customerId) {

        Response accountsResponse = ApiClient.get("/customers/" + customerId + "/accounts");
        requireOk(accountsResponse, "Get First AccountId");

        return accountsResponse.jsonPath().getString("[0].id");

    }

    public Response createAccount(String customerId, String accountType, String fromAccountId) {

        int typeCode = accountType.equalsIgnoreCase("SAVINGS") ? 1 : 0;

        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        params.put("newAccountType", typeCode);
        params.put("fromAccountId", fromAccountId);

        Response createResponse = ApiClient.postWithParams("/createAccount", params);

        requireOk(createResponse, "Create Bank Account");
        return createResponse;

    }

    private void requireOk(Response response, String operational) {
        if (response.getStatusCode() != 200) {
            throw new IllegalStateException(operational + "  failed: expected status code is 200 but got "
                    + response.getStatusCode() + ": " + response.asString());
        }
    }

}
