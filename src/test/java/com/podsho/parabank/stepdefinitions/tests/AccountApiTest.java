package com.podsho.parabank.stepdefinitions.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


import org.testng.annotations.Test;

import com.podsho.parabank.utils.ConfigReader;

public class AccountApiTest {

    private static final String BASE = ConfigReader.getProperty("base.url");

    @Test 
    public void GetExistingAccount_ReturnAccountDetails() {
        given()
            .accept("application/json")
        .when()
            .get(BASE + "/accounts/12345")
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("id", equalTo(12345))
            .body("customerId", equalTo(12212))
            .body("type", equalTo("CHECKING"))
            .body("balance", equalTo(-2901.0F));
    }

    @Test 
    public void negativeGetExistingAccount_ReturnAccountDetails(){
        given()
            .accept("application/json")
        .when()
            .get(BASE + "/accounts/99999")
        .then()
            .statusCode(400)
            .body(equalTo("Could not find account #99999"));
    
    }

    @Test 
    public void negativeGetExistingAccount_NotANumber(){
        given()
            .accept("application/json")
        .when()
            .get(BASE + "/accounts/abc")
        .then()
            .statusCode(404)
            .body(equalTo(""));
    
    }
}
