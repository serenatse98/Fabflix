package edu.uci.ics.sctse.service.idm.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import edu.uci.ics.sctse.service.idm.core.UserDatabase;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.validation.PassEmailChecker;

import java.sql.SQLException;

public class PrivilegeRequestModel
{
    private String email;
    private int plevel;

    public PrivilegeRequestModel() { }

    @JsonCreator
    public PrivilegeRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "plevel", required = true) int plevel)
    {
        this.email = email;
        this.plevel = plevel;
    }

    public int resultCode()
            throws SQLException
    {
        if (plevel <= 0 || plevel > 5)
        {
            ServiceLogger.LOGGER.info("plevel out of range");
            return -14;
        }
        else if (!PassEmailChecker.hasValidEmail(email) || email == null)
        {
            ServiceLogger.LOGGER.info("Invalid email format");
            return -11;
        }
        else if (email.length() < 0 || email.length() > 50)
        {
            ServiceLogger.LOGGER.info("email invalid length");
            return -10;
        }
        else if (!UserDatabase.isEmailInDatabase(email))
        {
            ServiceLogger.LOGGER.info("User not in DB");
            return 14;
        }
        else if (UserDatabase.getPriv(email) <= plevel)
        {
            ServiceLogger.LOGGER.info("Has sufficient priv levels");
            ServiceLogger.LOGGER.info("plevel: " + plevel);
            ServiceLogger.LOGGER.info("getPriv: " + UserDatabase.getPriv(email));
            return 140;
        }
        else if (UserDatabase.getPriv(email) > plevel)
        {
            ServiceLogger.LOGGER.info("Not sufficient priv levels");
            return 141;
        }


        return 0;
    }

    public String getEmail()
    {
        return email;
    }

    public int getPlevel()
    {
        return plevel;
    }
}
