package edu.uci.ics.sctse.service.idm.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.idm.core.UserDatabase;

import com.fasterxml.jackson.annotation.JsonCreator;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.validation.PassEmailChecker;

public class RegisterRequestModel
//    implements Validate
{
    private String email;
    private char[] password;
    private byte[] salt;
    private boolean isValidEmail;
    private boolean isValidPass;

    public RegisterRequestModel() { }

    @JsonCreator
    public RegisterRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "password", required = true) char[] password)
    {
        this.email = email;
        this.password = password;
    }

    public void checkEmailAndPassword()
    {
        PassEmailChecker.hasValidEmail(email);
        PassEmailChecker.hasValidPassword(password);
    }

    public int resultCode()
    {
        isValidEmail = PassEmailChecker.hasValidEmail(email);
        isValidPass = PassEmailChecker.hasValidPassword(password);

        if (password == null || password.length == 0)
        {
            ServiceLogger.LOGGER.info("Password is empty/null");
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

        else if (password.length < 7 || password.length > 16)
        {
            ServiceLogger.LOGGER.info("Invalid password length");
            return 12;
        }
        else if (!isValidPass)
        {
            ServiceLogger.LOGGER.info("Invalid password formatting");
            return 13;
        }
        else if (UserDatabase.isEmailInDatabase(email))
        {
            ServiceLogger.LOGGER.info("Email already in database");
            return 16;
        }
        else if (isValidPass && isValidEmail)
        {
            ServiceLogger.LOGGER.info("User email & password ok");
            return 110;
        }

        return 0;
    }



    @JsonProperty("email")
    public String getEmail()
    {
        return email;
    }

    public String getHashPass()
    {
        return HashPassModel.hashPassword(password);
    }

    public byte[] getSalt()
    {
        return HashPassModel.getSalt();
    }

}
