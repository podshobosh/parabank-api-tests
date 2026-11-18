package com.podsho.parabank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.podsho.parabank.utils.ConfigReader;

/**
 * Reads ParaBank's own database (HSQLDB) so a scenario can confirm the API
 * actually persisted what it reported. Connection settings come from the db.*
 * properties, so the same code can target another database by changing the
 * driver and URL.
 */
public class TransactionDao {

    /**
     * @return the newest transaction recorded for the account
     * @throws IllegalStateException if the account has no transactions
     */
    public Transaction getLatest(String accountId) throws SQLException {
        // LIMIT 1 works on HSQLDB, PostgreSQL and MySQL; Oracle needs FETCH FIRST 1 ROWS ONLY.
        String sql = "SELECT TYPE, AMOUNT, DESCRIPTION FROM TRANSACTION "
                + "WHERE ACCOUNT_ID = ? ORDER BY ID DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(
                    ConfigReader.getProperty("db.url"),
                    ConfigReader.getProperty("db.user"),
                    ConfigReader.getProperty("db.password", ""));
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(accountId));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setType(rs.getInt("TYPE"));
                    transaction.setAmount(rs.getBigDecimal("AMOUNT"));
                    transaction.setDescription(rs.getString("DESCRIPTION"));
                    return transaction;
                }
                throw new IllegalStateException("No transaction found for account " + accountId);
            }
        }
    }

}
