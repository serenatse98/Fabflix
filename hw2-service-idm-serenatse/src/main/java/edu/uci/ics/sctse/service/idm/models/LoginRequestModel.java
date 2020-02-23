package edu.uci.ics.sctse.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.idm.core.UserDatabase;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.validation.PassEmailChecker;

import java.sql.SQLException;

public class LoginRequestModel
{
    private String email;
    private char[] password;
    private boolean isValidEmail;

    public LoginRequestModel() { }

    @JsonCreator
    public LoginRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "password", required = true) char[] password)
    {
        this.email = email;
        this.password = password;
    }

    private boolean hasEmailInDB()
    {
        return UserDatabase.isEmailInDatabase(email);
    }

    public boolean isCorrectPass()
            throws SQLException
    {
        if (hasEmailInDB())
        {
            String salt = UserDatabase.getSalt(email);
            ServiceLogger.LOGGER.info("[LoginRequestModel] SALT: " + salt);

            String enteredPass = HashPassModel.unhashPassword(password, salt);

            ServiceLogger.LOGGER.info("Checking entered password");
            return enteredPass.equals(UserDatabase.getHashPass(email));
        }
        return false;
    }

    public int resultCode()
            throws SQLException
    {
        isValidEmail = PassEmailChecker.hasValidEmail(email);

        if (password == null || password.length == 0)
        {
            ServiceLogger.LOGGER.info("Password empty/null");
            return -12;
        }

        else if (!isValidEmail || email == null)
        {
            ServiceLogger.LOGGER.info("Wrong email format.");
            return -11;
        }

        else if (email.length() > 50)
        {
            ServiceLogger.LOGGER.info("Email too long.");
            return -10;
        }

        else if (!isCorrectPass() && UserDatabase.isEmailInDatabase(email))
        {
            ServiceLogger.LOGGER.info("Incorrect Password Entered");
            return 11;
        }

        else if (!UserDatabase.isEmailInDatabase(email))
        {
            ServiceLogger.LOGGER.info("User not in DB");
            return 14;
        }

        return 120;
    }


    @JsonProperty("email")
    public String getEmail()
    {
        return email;
    }
}
