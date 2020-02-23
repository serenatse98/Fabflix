package edu.uci.ics.sctse.service.api_gateway.database;

import edu.uci.ics.sctse.service.api_gateway.GatewayService;
import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static edu.uci.ics.sctse.service.api_gateway.GatewayService.*;

public class ResponseDatabase
{

    /*
        transactionid
        email - null
        sessionid - null
        response
        httpstatus - int
    */

    public static void insertResponseIntoDB(String transactionid, String response, String email, String sessionid, int httpstatus)
    {
        ServiceLogger.LOGGER.info("Inserting response into DB...");
        // request connection from connection pool
        Connection con = GatewayService.getConPool().requestCon();

        try
        {
            String query = "INSERT INTO responses (transactionid, response, email, sessionid, httpstatus) " +
                    "VALUES (?, ?, ?, ?, ?);";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, transactionid);
            ps.setString(2, response);
            ps.setString(3, email);
            ps.setString(4, sessionid);
            ps.setInt(5, httpstatus);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");

        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning(ANSI_YELLOW + "SQL Exception while inserting response." + ANSI_RESET);
        }
        // release connection when done
        GatewayService.getConPool().releaseCon(con);
    }

    public static Map<String, Object> getResponseFromDB(String transactionID)
    {
        ServiceLogger.LOGGER.info("Getting response from DB...");
        // request connection from connection pool
        Connection con = GatewayService.getConPool().requestCon();

        Map<String, Object> responseMap = new HashMap<>();

        try
        {
            String query = "SELECT transactionid, email, sessionid, response, httpstatus " +
                    "FROM responses WHERE transactionid = ?;";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, transactionID);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            if (rs.next())
            {
                ServiceLogger.LOGGER.info(ANSI_PURPLE + "TRANSACTION IN DB... MAKING MAP" + ANSI_RESET);
                responseMap.put("transactionid", rs.getString("transactionid"));
                responseMap.put("email", rs.getString("email"));
                responseMap.put("sessionid", rs.getString("sessionid"));
                responseMap.put("response", rs.getString("response"));
                responseMap.put("httpstatus", rs.getInt("httpstatus"));
            }

            return responseMap;

        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning(ANSI_YELLOW + "SQL Exception while inserting response." + ANSI_RESET);
            e.printStackTrace();
        }

        // release connection when done
        GatewayService.getConPool().releaseCon(con);

        return responseMap;
    }

    public static void deleteResponse(String transactionID)
    {
        // request connection from connection pool
        Connection con = GatewayService.getConPool().requestCon();
        try
        {
            ServiceLogger.LOGGER.info("Deleting response in DB...");
            String removeQuery = "DELETE FROM responses WHERE transactionid = ?;";

            PreparedStatement removePS = con.prepareStatement(removeQuery);
            removePS.setString(1, transactionID);

            ServiceLogger.LOGGER.info("Trying query: " + removePS.toString());
            removePS.execute();
            ServiceLogger.LOGGER.info("Query succeeded");
        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning(ANSI_YELLOW + "SQL Exception while inserting response." + ANSI_RESET);
            e.printStackTrace();
        }
        // release connection when done
        GatewayService.getConPool().releaseCon(con);
    }

    public static String getEmailFromDB(String transactionID)
    {
        return  "";
    }
}
