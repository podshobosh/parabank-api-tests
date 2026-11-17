package com.podsho.parabank.models;

public enum AccountType {
    CHECKING(0),
    SAVINGS(1);

    private final int code;

    AccountType(int code){this.code = code;}

    public int getCode(){
        return code;
    }
}
