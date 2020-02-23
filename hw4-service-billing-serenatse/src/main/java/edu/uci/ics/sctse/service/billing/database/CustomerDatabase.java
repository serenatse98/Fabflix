package edu.uci.ics.sctse.service.billing.database;

import edu.uci.ics.sctse.service.billing.BillingService;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.objects.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDatabase
{
    public static void insertCustomerIntoDB(String email, String firstName, String lastName, String ccid, String address)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Inserting customer into DB...");

        String query = "INSERT INTO customers (email, firstName, lastName, ccId, address) " +
                "VALUES (?, ?, ?, ?, ?);";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, firstName);
        ps.setString(3, lastName);
        ps.setString(4, ccid);
        ps.setString(5, address);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
    }

    public static boolean isCustomerInDB(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Checking if user's email is in DB...");

        String query = "SELECT * FROM customers WHERE email = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        return rs.next();
    }

    public static void updateCustomerInDB(String email, String firstName, String lastName, String ccid, String address)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Updating customer...");

        String query = "UPDATE customers SET firstName = ?, lastName = ?, ccId = ?, address = ? WHERE email = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ps.setString(3, ccid);
        ps.setString(4, address);
        ps.setString(5, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
    }

    public static Customer getCustomerFromDB(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Retrieving customer from DB...");

        String query = "SELECT email, firstName, lastName, ccId, address FROM customers WHERE email = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        Customer customer = new Customer();
        if (rs.next())
        {
            customer = new Customer(
                    rs.getString("email"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("ccId"),
                    rs.getString("address"));
        }

        return customer;

    }
}
