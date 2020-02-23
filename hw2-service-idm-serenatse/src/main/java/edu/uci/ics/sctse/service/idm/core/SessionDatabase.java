package edu.uci.ics.sctse.service.idm.core;

import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.security.Session;
import edu.uci.ics.sctse.service.idm.IDMService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SessionDatabase
{
    public static boolean insertSessionIntoDB(Session s)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Inserting user into database...");

        String query = "INSERT INTO cs122b_db143.sessions (sessionID, email, status, timeCreated, lastUsed, exprTime) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
        ps.setString(1, s.getSessionID().toString());
        ps.setString(2, s.getEmail());
        ps.setInt(3, 1);
        ps.setString(4, s.getTimeCreated().toString());
        ps.setString(5, s.getLastUsed().toString());
        ps.setString(6, s.getExprTime().toString());

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
        return true;

    }

    public static boolean isEmailInDB(String email)
    {
        try
        {
            ServiceLogger.LOGGER.info("Checking if user is already in DB");
            String query = "SELECT email FROM cs122b_db143.sessions WHERE email = ?";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Success. ");

            return rs.next();
        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query Failed: Unable to retrieve emails.");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSessionActive(String email, String sessionID)
    {
        try
        {
            ServiceLogger.LOGGER.info("Checking if user has active session...");
            String query = "SELECT * FROM cs122b_db143.sessions WHERE sessionID = ? AND email = ? AND status = 1";
            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setString(1, sessionID);
            ps.setString(2, email);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Success.");

            return rs.next();
        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query Failed: Unable to retrieve session.");
            e.printStackTrace();
        }

        return false;
    }

    public static String getSessionID(String email)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Getting sessionID for " + email);
        String query = "SELECT sessionID FROM cs122b_db143.sessions WHERE email = ?";
        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
        ps.setString(1, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        String sessionID = "";
        if (rs.next())
            sessionID = rs.getString("sessionID");

        return sessionID;
    }

    public static int getSessionStatus(String email, String sessionID)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Retrieving session status for " + email);

        String query = "SELECT status FROM cs122b_db143.sessions WHERE sessionID = ? AND email = ?";
        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
        ps.setString(1, sessionID);
        ps.setString(2, email);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        int status = 0;
        while (rs.next())
        {
            ServiceLogger.LOGGER.info("Getting status from resultSet");
            status = rs.getInt("status");
            ServiceLogger.LOGGER.info("Retrieved status: " + status);
        }
        return status;
    }

    public static void revokeSession(String email, String sessionID)
        throws SQLException
    {
        ServiceLogger.LOGGER.info("Revoking session...");
        String query = "UPDATE sessions SET status = 4 WHERE email = ? AND sessionID = ?";
        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, sessionID);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ps.execute();
        ServiceLogger.LOGGER.info("Query succeeded");
    }

    public static int checkStatus(String email, String sessionID)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Checking revoked times...");

        String query = "SELECT status, timeCreated, lastUsed, exprTime " +
                "FROM sessions WHERE email = ? AND sessionID = ?";
        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, sessionID);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        if (rs.next())
        {
            int SESSION_ACTIVE = 1;
            int SESSION_CLOSED = 2;
            int SESSION_EXPIRED = 3;
            int SESSION_REVOKED = 4;
            boolean isTimedOut;

            int sessionStatus = rs.getInt("status");
            Timestamp lastUsed = rs.getTimestamp("lastUsed");
            Timestamp exprTime = rs.getTimestamp("exprTime");
            Timestamp currTime = new Timestamp(System.currentTimeMillis());
            long timeout = IDMService.getConfigs().getTimeout();

            if (lastUsed.before(exprTime))
            {
                updateSessionStatus(email, sessionID, SESSION_ACTIVE, exprTime);
                return SESSION_ACTIVE;
            }
            else if (lastUsed.after(exprTime))
            {
                updateSessionStatus(email, sessionID, SESSION_EXPIRED, exprTime);
                return SESSION_EXPIRED;
            }
            else if ((currTime.getTime() - lastUsed.getTime()) > timeout)
            {
                isTimedOut = true;
                updateRevokedSession(email, sessionID, isTimedOut, exprTime);
                return SESSION_REVOKED;
            }
            else if ((currTime.getTime() - lastUsed.getTime()) < timeout)
            {
                isTimedOut = false;
                updateRevokedSession(email, sessionID, isTimedOut, exprTime);
                Session newSession = Session.createSession(email);
                insertSessionIntoDB(newSession);
                return SESSION_ACTIVE;
            }
        }

        return 0;
    }

    public static void updateRevokedSession(String email, String sessionID, boolean isTimedOut, Timestamp exprTime)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Updating revoked session...");
        int SESSION_ACTIVE = 1;
        int SESSION_REVOKED = 4;

        if (isTimedOut)
        {
            updateSessionStatus(email, sessionID, SESSION_REVOKED, exprTime);
        }
        else
        {
            updateSessionStatus(email, sessionID, SESSION_ACTIVE, exprTime);
        }
    }


    public static void updateSessionStatus(String email, String sessionID, int sessionStatus, Timestamp exprTime)//, SessionRequestModelOLD requestModel)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Updating session status...");

        int SESSION_ACTIVE = 1;
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        Timestamp newExprTime = new Timestamp(currTime.getTime() + exprTime.getTime());

        if (sessionStatus == SESSION_ACTIVE)
        {
            String q = "UPDATE sessions SET lastUsed = ? WHERE email = ? AND sessionID = ?; ";

            PreparedStatement activePS = IDMService.getCon().prepareStatement(q);
            activePS.setTimestamp(1, currTime);
            activePS.setString(2, email);
            activePS.setString(3, sessionID);

            ServiceLogger.LOGGER.info("Trying query: " + activePS.toString());
            activePS.execute();
            ServiceLogger.LOGGER.info("Query succeeded");
        }
        else
        {
            String query = "UPDATE sessions SET status = ? WHERE email = ? AND sessionID = ?;";

            PreparedStatement ps = IDMService.getCon().prepareStatement(query);
            ps.setInt(1, sessionStatus);
            ps.setString(2, email);
            ps.setString(3, sessionID);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");
        }

//        String query = "SELECT status, timeCreated, lastUsed, exprTime " +
//                "FROM cs122b_db143.sessions " +
//                "WHERE email = ? AND sessionID = ?";
//        PreparedStatement ps = IDMService.getCon().prepareStatement(query);
//        ps.setString(1, email);
//        ps.setString(2, sessionID);
//
//        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
//        ResultSet rs = ps.executeQuery();
//        ServiceLogger.LOGGER.info("Query succeeded");
//
//        if (rs.next())
//        {
////            ServiceLogger.LOGGER.info("Session for " + email + " found. Rebuilding session...");
////            Token t = Token.rebuildToken(sessionID);
////            Session s = Session.rebuildSession(email, t, rs.getTimestamp("timeCreated"),
////                    rs.getTimestamp("lastUsed"), rs.getTimestamp("exprTime"));
////            long currTime = System.currentTimeMillis();
////            long timeDiff = currTime - s.getLastUsed().getTime();
////            ServiceLogger.LOGGER.info("LAST    TIME: " + s.getLastUsed().getTime());
////            ServiceLogger.LOGGER.info("CURRENT TIME: " + currTime);
////            ServiceLogger.LOGGER.info("TIME  DIFFER: " + timeDiff);
//            int sessionStatus = rs.getInt("status");
//            Timestamp lastUsed = rs.getTimestamp("lastUsed");
//            Timestamp exprTime = rs.getTimestamp("exprTime");
//            Timestamp currTime = new Timestamp(System.currentTimeMillis());
//            long timeout = IDMService.getConfigs().getTimeout();
//
//            if (lastUsed.before(exprTime))
//            {
//
//            }
//            int SESSION_ACTIVE = 1;
//
//            if (sessionStatus == SESSION_ACTIVE)
//            {
//                if (s.isDataValid())
//                {
//                    ServiceLogger.LOGGER.info("Session is still active.");
//                    ServiceLogger.LOGGER.info("Updating last used...");
//                    s.update();
//
//                    String updateQuery = "UPDATE cs122b_db143.sessions " +
//                            "SET lastUsed = ? WHERE sessionID = ? AND email = ?";
//                    PreparedStatement updateSession = IDMService.getCon().prepareStatement(updateQuery);
//                    updateSession.setTimestamp(1, s.getLastUsed());
//                    updateSession.setString(2, sessionID);
//                    updateSession.setString(3, email);
//
//                    ServiceLogger.LOGGER.info("Trying query: " + updateSession.toString());
//                    updateSession.execute();
//                    ServiceLogger.LOGGER.info("Query succeeded");
//
//                    ServiceLogger.LOGGER.info("Session updated!");
//                }
//
//                else if (timeDiff > Session.SESSION_TIMEOUT)
//                {
//                    ServiceLogger.LOGGER.info("Session has timed out. User needs to login again.");
//                    ServiceLogger.LOGGER.info("Updating status to 4 (REVOKED) in DB...");
//                    String timeoutQuery = "UPDATE cs122b_db143.sessions " +
//                            "SET status = 4 WHERE sessionID = ? AND email = ?";
//                    PreparedStatement timeoutSession = IDMService.getCon().prepareStatement(timeoutQuery);
//                    timeoutSession.setString(1, sessionID);
//                    timeoutSession.setString(2, email);
//
//                    ServiceLogger.LOGGER.info("Trying query: " + timeoutSession.toString());
//                    timeoutSession.execute();
//                    ServiceLogger.LOGGER.info("Query succeeded");
//
//                    ServiceLogger.LOGGER.info("Session revoked!");
//                }
//
//                else if (currTime > Session.TOKEN_EXPR)
//                {
//                    ServiceLogger.LOGGER.info("Session has expired. User needs to login again.");
//                    ServiceLogger.LOGGER.info("Updating status to 3 (EXPIRED) in DB...");
//                    String timeoutQuery = "UPDATE cs122b_db143.sessions " +
//                            "SET status = 3 WHERE sessionID = ? AND email = ?";
//                    PreparedStatement expireSession = IDMService.getCon().prepareStatement(timeoutQuery);
//                    expireSession.setString(1, sessionID);
//                    expireSession.setString(2, email);
//
//                    ServiceLogger.LOGGER.info("Trying query: " + expireSession.toString());
//                    expireSession.execute();
//                    ServiceLogger.LOGGER.info("Query succeeded");
//
//                    ServiceLogger.LOGGER.info("Session revoked!");
//                }
//            }
//            else
//            {
//                //return the session status
//            }
//        }
    }
}

