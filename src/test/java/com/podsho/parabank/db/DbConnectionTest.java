package com.podsho.parabank.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.testng.Assert;
import com.podsho.parabank.utils.ConfigReader;
import org.testng.annotations.Test;

public class DbConnectionTest {

    /** Reads db.<profile>.healthcheck, because the smoke query differs per database dialect. */
    private String healthCheckQuery() {
        String profile = ConfigReader.getProperty("db.profile", "hsqldb");
        return ConfigReader.getProperty("db." + profile + ".healthcheck", "SELECT 1");
    }

    @Test
    public void testConnection() throws SQLException {
        try (Connection c = DbUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(healthCheckQuery())) {

            try (ResultSet rs = ps.executeQuery()) {
                Assert.assertTrue(rs.next());
                Assert.assertTrue(rs.getInt(1) >= 1);
            }
        }
    }

}
