package edu.uci.ics.sctse.service.billing.database;

import edu.uci.ics.sctse.service.billing.BillingService;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.objects.Items;

import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartDatabase
{
    public static void insertIntoCartDB(String email, String movieId, int quantity)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Inserting item into endpoints...");

        String query = "INSERT INTO carts (email, movieId, quantity) " +
                "VALUES (?, ?, ?);";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, movieId);
        ps.setInt(3, quantity);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
    }

    public static boolean updateCartDB(String email, String movieId, int quantity)
        throws SQLException
    {
        ServiceLogger.LOGGER.info("Updating endpoints...");

        String check = "SELECT * FROM carts WHERE email = ? ;";
        PreparedStatement checkPS = BillingService.getCon().prepareCall(check);
        checkPS.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + checkPS.toString());
        ResultSet rs = checkPS.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        String query;
        if (rs.next())
        {
            String check2 = "SELECT * FROM carts WHERE email = ? AND movieId = ?;";
            PreparedStatement checkPS2 = BillingService.getCon().prepareCall(check2);
            checkPS2.setString(1, email);
            checkPS2.setString(2, movieId);

            ServiceLogger.LOGGER.info("Trying query: " + checkPS.toString());
            ResultSet rs2 = checkPS2.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            if (rs2.next())
            {
                query = "UPDATE carts SET quantity = ? WHERE email = ? AND movieId = ?;";
                PreparedStatement ps = BillingService.getCon().prepareStatement(query);
                ps.setInt(1, quantity);
                ps.setString(2, email);
                ps.setString(3, movieId);

                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                ps.execute();
                ServiceLogger.LOGGER.info("Query succeeded");
                return true;
            }
        }

        return false;
    }

    public static boolean deleteFromCartDB(String email, String movieId)
        throws SQLException
    {
        ServiceLogger.LOGGER.info("Deleting from carts...");

        String check = "SELECT * FROM carts WHERE email = ? AND movieId = ?;";
        PreparedStatement checkPS = BillingService.getCon().prepareCall(check);
        checkPS.setString(1, email);
        checkPS.setString(2, movieId);

        ServiceLogger.LOGGER.info("Trying query: " + checkPS.toString());
        ResultSet rs = checkPS.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        if (rs.next())
        {
            String query = "DELETE FROM carts WHERE email = ? AND movieId = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, movieId);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");
            return true;
        }

        return false;
    }

    public static Items[] retrieveFromCartDB(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Retrieving items from endpoints...");

        String query = "SELECT email, movieId, quantity FROM carts WHERE email = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        ServiceLogger.LOGGER.info("Making item list...");
        ArrayList<Items> itemsArrayList = new ArrayList<>();
        while (rs.next())
        {
            Items item = new Items(
                    rs.getString("email"),
                    rs.getString("movieId"),
                    rs.getInt("quantity"));
            ServiceLogger.LOGGER.info("ITEMS: " + item);
            itemsArrayList.add(item);
        }

        Items[] itemList = itemsArrayList.toArray(new Items[itemsArrayList.size()]);
        return itemList;
    }

    public static boolean isEmailInCartsDB(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Checking email in carts...");

        String query = "SELECT * FROM carts WHERE email = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        return rs.next();
    }

    public static void clearFromCartDB(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Clearing endpoints for " + email + "...");

        String query = "DELETE FROM carts WHERE email = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
    }

    public static String getSumOfItems(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Getting sum of items in cart...");

        String query = "SELECT M.unit_price, M.discount, C.quantity " +
                "FROM carts AS C, movie_prices AS M " +
                "WHERE email = ? AND M.movieId = C.movieId; ";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        ServiceLogger.LOGGER.info("Getting sum...");
        float sum = 0;
        while (rs.next())
        {
            float unit_price = rs.getFloat("M.unit_price");
            float discount = rs.getFloat("M.discount");
            int quantity = rs.getInt("C.quantity");

            ServiceLogger.LOGGER.info("UNIT PRICE: " + unit_price);
            ServiceLogger.LOGGER.info("DISCOUNT: " + discount);
            ServiceLogger.LOGGER.info("QUANTITY: " + quantity);

            sum += unit_price * discount * quantity;

        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        ServiceLogger.LOGGER.info("CART TOTAL: " + sum);
        return df.format(sum);
    }
}
