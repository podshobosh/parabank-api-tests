package com.podsho.parabank.testdata;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/** Loads named test cases from JSON files under testdata/, keeping data out of the feature files. */
public class TestData {

    public static LoginCase invalidLogin(String name) {
        try (InputStream in = TestData.class.getClassLoader().getResourceAsStream("testdata/invalidLogins.json")) {
            if (in == null) {
                throw new IllegalArgumentException("testdata/invalidLogins.json not found on the classpath");
            }

            LoginCase[] cases = new ObjectMapper().readValue(in, LoginCase[].class);

            for (LoginCase c : cases) {
                if (c.getName().equals(name)) {
                    return c;
                }
            }
            throw new IllegalArgumentException("No login case named: " + name);
        } catch (IOException e) {
            throw new RuntimeException("Could not read invalidLogins.json", e);
        }

    }

}
