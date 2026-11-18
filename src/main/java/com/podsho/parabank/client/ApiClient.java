package com.podsho.parabank.client;

import java.util.Map;

import com.podsho.parabank.utils.ConfigReader;
import com.podsho.parabank.utils.ApiLogContext;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiClient {

    // final base URL shared through all the calls
    private static final String BASE_URL = ConfigReader.getProperty("base.url");

    private static RequestSpecification baseRequest() {
        return given()
                .filter(new RequestLoggingFilter(ApiLogContext.stream()))
                .filter(new ResponseLoggingFilter(ApiLogContext.stream()))
                .baseUri(BASE_URL)
                .accept("application/json");
    }

    // GET response : takes in given endpoint
    public static Response get(String endpoint) {
        return baseRequest()
                .when()
                .get(endpoint);
    }

    public static Response post(String endpoint, Object requestBody) {
        return baseRequest()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    public static Response put(String endpoint, Object requestBody) {
        return baseRequest()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put(endpoint);
    }

    public static Response delete(String endpoint) {
        return baseRequest()
                .when()
                .delete(endpoint);
    }

    public static Response postWithParams(String endpoint, Map<String, Object> queryParams) {
        return baseRequest()
                .queryParams(queryParams)
                .when()
                .post(endpoint);
    }

}
