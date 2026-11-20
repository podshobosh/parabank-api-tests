package com.podsho.parabank.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Reads the ACCOUNT table directly, so a scenario can compare the balance the
 * database stores with the one the API reports.
 */
public class AccountDao {

    /**
     * @return the balance stored in the ACCOUNT table
     * @throws IllegalStateException if no account has that id
     */
    public BigDecimal getBalance(String accountId) throws SQLException {
        // BALANCE is DECIMAL; read it as BigDecimal so no precision is lost to a double.
        String sql = "SELECT BALANCE FROM ACCOUNT WHERE ID = ?";

        try (Connection c = DbUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(accountId));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("BALANCE");
                }
                throw new IllegalStateException("No acount found with this id: " + accountId);
            }
        }
    }

}
