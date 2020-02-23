package edu.uci.ics.sctse.service.billing.core;


import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;

import java.sql.Date;

public class Validate
{
    public static boolean hasValidEmail(String email)
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

    public static boolean hasValidCreditcardLen(String ccid)
    {
        return ccid.length() >= 16 && ccid.length() <= 20;
    }

    public static boolean isValidCreditcardID(String ccid)
    {
        ServiceLogger.LOGGER.info("trying to turn " + ccid + " into int");

        return ccid.matches("[0-9]{16,20}");
    }

    public static boolean isValidExpiration(Date date)
    {
        Date today = new Date(System.currentTimeMillis());
        int temp = date.compareTo(today);

        return temp >= 0;
    }
}
