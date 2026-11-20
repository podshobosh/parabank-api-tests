package com.podsho.parabank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.podsho.parabank.utils.ConfigReader;

/**
 * Opens JDBC connections from the db.* properties, so the DAOs never hold
 * connection details and the target database changes through config alone.
 */
public class DbUtils {

    /**
     * Returns a new connection on every call. The caller must close it,
     * ideally in a try-with-resources header.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(ConfigReader.getProperty("db.url"),
                ConfigReader.getProperty("db.user"),
                ConfigReader.getProperty("db.password", ""));
    }
}
