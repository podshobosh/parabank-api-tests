package com.podsho.parabank.utils;

import io.restassured.response.Response;

/**
 * Shared state for a single scenario.
 *
 * Picocontainer creates one instance per scenario and injects the same object
 * into every step-definition class that asks for it in its constructor, so
 * steps in different classes can read and write the same response.
 */
public class ScenarioContext {

    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
