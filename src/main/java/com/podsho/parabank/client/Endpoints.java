package com.podsho.parabank.client;

/** ParaBank endpoint paths, relative to base.url. */
public final class Endpoints {

    public static final String DEPOSIT = "/deposit";
    public static final String CREATE_ACCOUNT = "/createAccount";
    public static final String TRANSFER = "/transfer";

    public static String account(String accountId) {
        return "/accounts/" + accountId;
    }

    public static String login(String username, String password) {
        return "/login/" + username + "/" + password;
    }

    public static String customerAccounts(String customerId) {
        return "/customers/" + customerId + "/accounts";
    }

    private Endpoints() {
    }

}
