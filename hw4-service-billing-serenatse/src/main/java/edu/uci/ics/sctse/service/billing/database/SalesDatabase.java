package edu.uci.ics.sctse.service.billing.database;

import edu.uci.ics.sctse.service.billing.BillingService;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.objects.Order;

import java.sql.*;
import java.util.ArrayList;

public class SalesDatabase
{
    public static void insertOrderInDB(String email, String token)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Inserting new sale & transaction into DB...");

        String cartItems = "SELECT email, movieId, quantity FROM carts WHERE email = ?;";
        PreparedStatement psCart = BillingService.getCon().prepareCall(cartItems);
        psCart.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + psCart.toString());
        ResultSet rs = psCart.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        Date today = new Date(System.currentTimeMillis());
        while (rs.next())
        {
            String query = "CALL insert_sales_transactions (?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = BillingService.getCon().prepareCall(query);
            ps.setString(1, email);
            ps.setString(2, rs.getString("movieId"));
            ps.setInt(3, rs.getInt("quantity"));
            ps.setDate(4, today);
            ps.setString(5, token);
            ps.setNull(6, Types.VARCHAR);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");

        }

        CartDatabase.clearFromCartDB(email);
    }

    public static Order[] getOrderItemsFromDB(String email, String tID)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Getting order info from DB...");

        String query = "SELECT S.email, S.movieId, S.quantity, S.saleDate, M.unit_price, M.discount " +
                "FROM sales AS S, movie_prices AS M, transactions AS T " +
                "WHERE email = ? AND S.movieId = M.movieId AND T.transactionId = ? AND T.sId = S.id;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, tID);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        ArrayList<Order> orderArrayList = new ArrayList<>();
        while (rs.next())
        {
            Order order = new Order(
                    rs.getString("S.email"),
                    rs.getString("S.movieId"),
                    rs.getInt("S.quantity"),
                    rs.getFloat("M.unit_price"),
                    rs.getFloat("M.discount"),
                    rs.getDate("S.saleDate"));
            orderArrayList.add(order);
        }

        Order[] orderList = orderArrayList.toArray(new Order[orderArrayList.size()]);
        return orderList;
    }

    public static float[] getMoviePrice(String movieId)
            throws SQLException
    {
        String query = "SELECT unit_price, discount FROM movie_prices WHERE movieId = ?;";
        PreparedStatement ps = BillingService.getCon().prepareStatement(query);
        ps.setString(1, movieId);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        float[] prices = new float[2];
        if (rs.next())
        {
            prices[0] = rs.getFloat("unit_price");
            prices[1] = rs.getFloat("discount");
        }

        return prices;
    }
}

