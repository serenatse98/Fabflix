package edu.uci.ics.sctse.service.idm.core;

import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.models.RegisterRequestModel;
import edu.uci.ics.sctse.service.idm.IDMService;
import org.apache.commons.codec.binary.Hex;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDatabase
{
    public static boolean isEmailInDatabase(String email)
    {
        try
        {
            String query = "SELECT email FROM cs122b_db143.users WHERE email = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Success. ");

            return rs.next();
        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query Failed: Unable to retrieve user emails.");
            e.printStackTrace();
        }
        return false;
    }


    public static boolean insertUserIntoDB(RegisterRequestModel requestModel)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Inserting user into database...");

        String query = "INSERT INTO cs122b_db143.users (email, status, plevel, salt, pword) VALUES (?, ?, ?, ?, ?)";

        String hashPass = requestModel.getHashPass();
        ServiceLogger.LOGGER.info("done hashing password & turn to string");

        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
        ServiceLogger.LOGGER.info("prepared sql statement");
        ps.setString(1, requestModel.getEmail());
        ps.setInt(2, 1);
        ps.setInt(3, 5);

        ServiceLogger.LOGGER.info("Encoding salt");
        String encodedSalt = Hex.encodeHexString(requestModel.getSalt());
        ServiceLogger.LOGGER.info("Done encoding salt.");
        ServiceLogger.LOGGER.info("Salt: " + encodedSalt);
        ps.setString(4, encodedSalt);

        ps.setString(5, hashPass);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
        return true;

    }

    public static String getSalt(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Retrieving salt from database...");

        String query = "SELECT salt FROM cs122b_db143.users WHERE email = ?";
        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
//        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");

        String salt = "";
        if (rs.next())
        {
            ServiceLogger.LOGGER.info("Getting salt from resultSet");
            salt = rs.getString("salt");
        }

        return salt;
    }

    public static String getHashPass(String email)
        throws SQLException
    {
        ServiceLogger.LOGGER.info("Retrieving hashed password from database...");

        String query = "SELECT pword FROM cs122b_db143.users WHERE email = ?";
        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        String password = "";
        if (rs.next())
        {
            ServiceLogger.LOGGER.info("Getting password from resultSet");
            password = rs.getString("pword");
        }

        return password;
    }

    public static int getPriv(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Retrieving privilege level from database...");

        String query = "SELECT plevel FROM cs122b_db143.users WHERE email = ?";
        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        int privlvl = 0;
        if (rs.next())
        {
            ServiceLogger.LOGGER.info("Getting password from resultSet");
            privlvl = rs.getInt("plevel");
        }

        return privlvl;
    }

}
