package edu.uci.ics.sctse.service.billing.database;

import edu.uci.ics.sctse.service.billing.BillingService;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.objects.Creditcard;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreditcardDatabase
{
    public static void insertIntoCCDB(String id, String firstName, String lastName, Date expiration)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Inserting credit card into DB...");

        String query = "INSERT INTO creditcards (id, firstName, lastName, expiration) " +
                "VALUES (?, ?, ?, ?);";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, id);
        ps.setString(2, firstName);
        ps.setString(3, lastName);
        ps.setDate(4, expiration);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
    }

    public static boolean isCCinDB(String ccid)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Checking if credit card is in DB...");

        String query = "SELECT * FROM creditcards WHERE id = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, ccid);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        return rs.next();
    }

    public static void updateCCinDB(String id, String firstName, String lastName, Date expiration)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Updating creditcard...");

        String query = "UPDATE creditcards SET firstName = ?, lastName = ?, expiration = ? WHERE id = ?; ";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.setDate(3, expiration);
        ps.setString(4, id);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
    }

    public static void deleteCCCinDB(String id)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Deleting credicard...");

        String query = "DELETE FROM creditcards WHERE id = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, id);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
    }

    public static Creditcard getCreditcardInfo(String id)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Getting card info...");

        String query = "SELECT id, firstName, lastName, expiration FROM creditcards WHERE id = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, id);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        Creditcard creditcard = new Creditcard();
        if (rs.next())
        {
             creditcard = new Creditcard(
                    rs.getString("id"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getDate("expiration"));
        }

        return creditcard;
    }
}
