package edu.uci.ics.sctse.service.idm.validation;

import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;

public class PassEmailChecker
{

    static public boolean hasValidPassword(char[] password)
    {
        if (password == null)
            return false;
        ServiceLogger.LOGGER.info("Testing password validity");
        String passPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[`~!@#$%^&*()\\-=_+{}\\[\\]\\\\|:;\"'<>,.?/]).*$";
        String pass = new String(password);
        if (!pass.matches(passPattern))
        {
            ServiceLogger.LOGGER.info("Password invalid.");
            return false;
        }

        ServiceLogger.LOGGER.info("PASSWORD VALID.");
        return true;
    }

    static public boolean hasValidEmail(String email)
    {
        ServiceLogger.LOGGER.info("Testing password validity");
        String emailPattern = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        // pattern from https://emailregex.com/
        if (!email.matches(emailPattern))
        {
            ServiceLogger.LOGGER.info("Invalid email format!!!");
            return false;
        }

        ServiceLogger.LOGGER.info("EMAIL: " + email);
        return true;
    }
}
