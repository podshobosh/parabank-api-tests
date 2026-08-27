package com.podsho.parabank.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();
    private static final String CONFIG_PATH = "src/main/java/com/podsho/parabank/resources/config/config.properties";

    private ConfigReader() {
    }

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream classpathStream = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (classpathStream != null) {
                properties.load(classpathStream);
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load classpath config/config.properties", e);
        }

        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_PATH, e);
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
