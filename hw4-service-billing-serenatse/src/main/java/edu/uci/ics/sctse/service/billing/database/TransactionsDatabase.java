package edu.uci.ics.sctse.service.billing.database;

import edu.uci.ics.sctse.service.billing.BillingService;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionsDatabase
{
    public static boolean isTokenInDB(String token)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Checking if token is in DB...");

        String query = "SELECT * FROM transactions WHERE token = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, token);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        return rs.next();
    }

    public static void insertTransIDIntoDB(String tID, String token)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Updating transaction ID...");

        String query = "UPDATE transactions SET transactionId = ? WHERE token = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, tID);
        ps.setString(2, token);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
    }

    public static ArrayList<String> getTransID(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Getting transaction ID...");

        String query = "SELECT DISTINCT transactionId FROM sales, transactions WHERE email = ? AND id = sId;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        ArrayList<String> tIDs = new ArrayList<>();
        while(rs.next())
        {
            tIDs.add(rs.getString("transactionId"));
        }

        ServiceLogger.LOGGER.info("TRANSIDS: " + tIDs.toString());
        return tIDs;
    }
}
