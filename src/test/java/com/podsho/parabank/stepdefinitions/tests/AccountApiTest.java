package com.podsho.parabank.stepdefinitions.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.testng.Assert;
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
    public void getAccount_nonExistentId_returns400WithNotFoundMessage(){
        given()
            .accept("application/json")
        .when()
            .get(BASE + "/accounts/99999")
        .then()
            .statusCode(400)
            .body(equalTo("Could not find account #99999"));
    
    }

    @Test 
    public void getAccount_nonNumericId_returns404(){
        given()
            .accept("application/json")
        .when()
            .get(BASE + "/accounts/abc")
        .then()
            .statusCode(404)
            .body(equalTo(""));
    
    }

    @Test 
    public void deposit_validAmount(){

        // 1. balance before
        float initBalance = given()
                .accept("application/json")
            .when()
                .get(BASE + "/accounts/12345")
            .then()
                .body("balance", instanceOf(Float.class))
                .extract().path("balance");
        
        // 2. deposit: fill in the endpoint, query params, status, and message check
        given()
            .accept("application/json")
            .queryParam("accountId", 12345)
            .queryParam("amount", 100)
        .when()
            .post(BASE + "/deposit")
        .then()
            .statusCode(200)   
            .body(equalTo("Successfully deposited $100 to account #12345"));

        
       float newBalance = given()
                .accept("application/json")
            .when()
                .get(BASE + "/accounts/12345")
            .then()
                .body("balance", instanceOf(Float.class))
                .extract().path("balance");
        
        
        Assert.assertEquals(newBalance, initBalance + 100, 0.001);
    }
}
