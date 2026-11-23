package com.podsho.parabank.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    private ConfigReader() {
    }

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config/config.properties not found in class path");
            }
            properties.load(in);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load classpath config/config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getProperty(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (hasText(systemValue)) {
            return systemValue.trim();
        }

        String envKey = key.toUpperCase().replace('.', '_');
        String envValue = System.getenv(envKey);
        if (hasText(envValue)) {
            return envValue.trim();
        }

        String fileValue = properties.getProperty(key);
        return hasText(fileValue) ? fileValue.trim() : defaultValue;
    }

    public static int getIntProperty(String key) {
        return getIntProperty(key, 0);
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (!hasText(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid integer value for property: " + key + " = " + value, e);
        }
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return hasText(value) ? Boolean.parseBoolean(value.trim()) : defaultValue;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
