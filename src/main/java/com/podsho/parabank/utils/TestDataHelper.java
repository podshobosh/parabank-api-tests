package com.podsho.parabank.utils;

import java.util.Random;
import java.util.UUID;

public class TestDataHelper {

    private static final String[] FIRST_NAMES = {"John", "Jane", "Michael", "Sarah", "David", "Emily"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Davis", "Miller"};

    private static final String[] STREET_NAMES = {"Main St", "Oak Ave", "South St"};
    private static final String[] CITIES = {"Khorog", "Dushanbe", "New York", "Tokyo", "London", "Barcelona"};
    private static final String[] STATES = {"CA", "VA", "NY", "SC", "NC", "MD"};

    private static final Random RANDOM = new Random();

    public static String randomFirstName() {
        return FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)];
    }

    public static String randomLastName() {
        return LAST_NAMES[RANDOM.nextInt(LAST_NAMES.length)];
    }

    public static String randomCity() {
        return CITIES[RANDOM.nextInt(CITIES.length)];
    }

    public static String randomState() {
        return STATES[RANDOM.nextInt(STATES.length)];
    }

    public static String randomPhoneNumber() {
        return "555" + String.format("%07d", RANDOM.nextInt(10_000_000));
    }

    public static String randomZipCode() {
        return String.format("%05d", RANDOM.nextInt(100_000));
    }

    public static String randomSSN() {
        return String.format("%09d", RANDOM.nextInt(1_000_000_000));
    }

    public static String randomAddress() {
        int houseNumber = RANDOM.nextInt(9999) + 1;
        return houseNumber + " " + STREET_NAMES[RANDOM.nextInt(STREET_NAMES.length)];
    }

    // returns a random username with 9 chars
    public static String randomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String randomPassword() {
        return "pass_#" + UUID.randomUUID().toString().substring(0, 4);
    }

    // returns a ramdom email 
    public static String randomEmail() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    }

}
