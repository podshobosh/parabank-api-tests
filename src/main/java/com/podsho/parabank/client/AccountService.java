package com.podsho.parabank.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.podsho.parabank.models.AccountType;

import io.restassured.response.Response;

/**
 * ParaBank operations (login, accounts, deposit) on top of {@link ApiClient}.
 * Step definitions call these methods so endpoint paths and parameter names
 * live in one place.
 *
 * Methods that return a value ({@link #login}, {@link #getBalance},
 * {@link #getFirstAccountId}) throw if the call fails. Methods that return the
 * raw {@link Response} ({@link #deposit}) leave status assertions to the
 * caller, so negative scenarios can assert on a 4xx.
 */
public class AccountService {

    private final ApiClient client;

    private static final Object CREATE_LOCK = new Object();

    public AccountService(ApiClient client) {
        this.client = client;
    }

    /**
     * @return the current balance of the account
     * @throws IllegalStateException if the lookup does not return 200
     */
    public BigDecimal getBalance(String accountId) {
        Response getResponse = getAccount(accountId);
        requireOk(getResponse, "Get balance");

        // Built from text so the exact decimal value survives; a double would not.
        return new BigDecimal(getResponse.jsonPath().getString("balance"));
    }

    /**
     * Deposits into an account. Returns the raw response without checking the
     * status, because invalid deposits are asserted on by the scenarios.
     */
    public Response deposit(String accountId, BigDecimal amount) {

        Map<String, Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("amount", amount);
        return client.postWithParams(Endpoints.DEPOSIT, params);
    }

    public Response getAccount(String accountId) {
        return client.get(Endpoints.account(accountId));
    }

    public Response loginRaw(String username, String password) {
        return client.get(Endpoints.login(username, password));
    }

    /**
     * @return the customer id of the logged-in user
     * @throws IllegalStateException if the credentials are rejected
     */
    public String loginAndGetCustomerId(String username, String password) {

        Response loginResponse = loginRaw(username, password);
        requireOk(loginResponse, "Login");
        return loginResponse.jsonPath().getString("id");
    }

    /**
     * @return the id of the customer's first account, used as the funding
     *         source when creating a new account
     * @throws IllegalStateException if the accounts cannot be listed
     */
    public String getFirstAccountId(String customerId) {

        Response accountsResponse = client.get(Endpoints.customerAccounts(customerId));
        requireOk(accountsResponse, "Listing accounts");

        return accountsResponse.jsonPath().getString("[0].id");
    }

    /**
     * Creates an account funded from an existing one.
     *
     * @return the create-account response, including the new account's id
     * @throws IllegalStateException if ParaBank does not return 200
     */
    public Response createAccount(String customerId, AccountType accountType, String fromAccountId) {

        Map<String, Object> params = new HashMap<>();
        params.put("customerId", customerId);
        params.put("newAccountType", accountType.getCode());
        params.put("fromAccountId", fromAccountId);

        Response createResponse;

        // ParaBank's createAccount fails intermittently when called concurrently,
        // so account creation is serialized while scenarios still run in parallel.
        synchronized (CREATE_LOCK) {
            createResponse = client.postWithParams(Endpoints.CREATE_ACCOUNT, params);
        }

        requireOk(createResponse, "Create account");
        return createResponse;
    }

    /**
     * Fails fast with the operation name, status and body. Without this, a
     * failed call surfaces later as a confusing JSON parse error, because
     * ParaBank returns plain text on errors.
     */
    private void requireOk(Response response, String operation) {
        if (response.getStatusCode() != 200) {
            throw new IllegalStateException(operation + " failed: expected status code 200 but got "
                    + response.getStatusCode() + ": " + response.asString());
        }
    }

}
