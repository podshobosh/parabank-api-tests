package com.podsho.parabank.client;

import com.podsho.parabank.utils.ConfigReader;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class ApiClient {


    // final base URL shared through all the calls
    private static final String BASE_URL = ConfigReader.getProperty("base.url");

    // GET response : takes in given endpoint
    public static Response get(String endpoint) {
        return given()
                .when()
                .get(BASE_URL + endpoint);
    }

    public static Response post(String endpoint, Object requestBody) {
        return given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(BASE_URL + endpoint);
    }

    public static Response put(String endpoint, Object requestBody){
        return given()
        .contentType("application/json")
        .body(requestBody)
        .when()
        .put(BASE_URL + endpoint);
    }

    public static Response delete(String endpoint){
        return given()
                .when()
                .delete(BASE_URL + endpoint);
    }

}
