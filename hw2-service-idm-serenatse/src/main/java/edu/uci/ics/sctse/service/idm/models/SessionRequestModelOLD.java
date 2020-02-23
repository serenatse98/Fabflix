package edu.uci.ics.sctse.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.idm.core.SessionDatabase;
import edu.uci.ics.sctse.service.idm.core.UserDatabase;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.validation.PassEmailChecker;

import java.sql.SQLException;

public class SessionRequestModelOLD
{
    private String email;
    private String sessionID;

    @JsonCreator
    public SessionRequestModelOLD(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "sessionID", required = true) String sessionID)
    {
        this.email = email;
        this.sessionID = sessionID;
    }

    public int resultCode()
            throws SQLException
    {
        if (sessionID.length() <= 0)
        {
            ServiceLogger.LOGGER.config("Token size is <= 0.");
            return -13;
        }
        else if (!PassEmailChecker.hasValidEmail(email) || email == null)
        {
            ServiceLogger.LOGGER.info("Wrong email format.");
            return -11;
        }

        else if (email.length() > 50)
        {
            ServiceLogger.LOGGER.info("Email too long.");
            return -10;
        }

        else if (!UserDatabase.isEmailInDatabase(email))
        {
            ServiceLogger.LOGGER.info("User not in DB");
            return 14;
        }

        else if (SessionDatabase.getSessionStatus(email, sessionID) == 1)
        {
            ServiceLogger.LOGGER.info("Status got = 1 (ACTIVE)");
            return 130;
        }

        else if (SessionDatabase.getSessionStatus(email, sessionID) == 3)
        {
            ServiceLogger.LOGGER.info("Status got = 3 (EXPIRED)");
            return 131;
        }

        else if (SessionDatabase.getSessionStatus(email, sessionID) == 2)
        {
            ServiceLogger.LOGGER.info("Status got = 2 (CLOSED)");
            return 132;
        }

        else if (SessionDatabase.getSessionStatus(email, sessionID) == 4)
        {
            ServiceLogger.LOGGER.info("Status got = 4 (REVOKED)");
            return 133;
        }

        ServiceLogger.LOGGER.info("Session not found.");
        return 134;
    }

    public String getEmail()
    {
        return email;
    }

    public String getSessionID()
    {
        return sessionID;
    }
}
