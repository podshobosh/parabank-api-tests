package com.podsho.parabank.client;

import java.util.Map;

import com.podsho.parabank.utils.ConfigReader;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiClient {

    private static RequestSpecification baseRequest() {
    return given()
            .filter(new RequestLoggingFilter())
            .filter(new ResponseLoggingFilter())
            .accept("application/json");
}

    // final base URL shared through all the calls
    private static final String BASE_URL = ConfigReader.getProperty("base.url");

    // GET response : takes in given endpoint
    public static Response get(String endpoint) {
        return baseRequest()
                .when()
                .get(BASE_URL + endpoint);
    }

    public static Response post(String endpoint, Object requestBody) {
        return baseRequest()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(BASE_URL + endpoint);
    }

    public static Response put(String endpoint, Object requestBody) {
        return baseRequest()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put(BASE_URL + endpoint);
    }

    public static Response delete(String endpoint) {
        return baseRequest()
                .when()
                .delete(BASE_URL + endpoint);
    }

    public static Response postWithParams(String endpoint, Map<String, Object> queryParams) {
        return baseRequest()
                .queryParams(queryParams)
                .when()
                .post(BASE_URL + endpoint);
    }

}
