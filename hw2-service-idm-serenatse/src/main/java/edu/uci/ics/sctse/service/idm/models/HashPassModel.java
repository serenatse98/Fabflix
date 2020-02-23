package edu.uci.ics.sctse.service.idm.models;

import edu.uci.ics.sctse.service.idm.security.Crypto;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

public class HashPassModel
{
    static private byte[] salt;

    static public String unhashPassword(char[] password, String salt)
    {
        String pword = "";

        try
        {
            ServiceLogger.LOGGER.info("Converting salt string to byte[]");
            byte[] saltBytes = Hex.decodeHex(salt);
            ServiceLogger.LOGGER.info("[HashPassModel] SALT: " + salt);

            ServiceLogger.LOGGER.info("Salt: " + salt);
            ServiceLogger.LOGGER.info("Unhashing password...");
            byte[] hashedPass = Crypto.hashPassword(password, saltBytes, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
            pword = Hex.encodeHexString(hashedPass);
            ServiceLogger.LOGGER.info("Unhashed password.");

        }
        catch (DecoderException e)
        {
            ServiceLogger.LOGGER.warning("[HashPassModel] Decoder exception.");
        }

        return pword;
    }

    static public String hashPassword(char[] password)
    {
        ServiceLogger.LOGGER.info("Getting salt...");
        salt = Crypto.genSalt();
        ServiceLogger.LOGGER.info("Salt: " + salt);
        ServiceLogger.LOGGER.info("Hashing password...");
        byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
        String pword = getHashedPass(hashedPassword);
        ServiceLogger.LOGGER.info("Hashed password.");

        return pword;
    }

    static private String getHashedPass(byte[] hashedPassword)
    {
        ServiceLogger.LOGGER.info("Turning byte[] into String");
        StringBuffer buf = new StringBuffer();
        for (byte b : hashedPassword)
        {
            buf.append(format(Integer.toHexString(Byte.toUnsignedInt(b))));
        }
        return buf.toString();
    }

    static private String format(String binS)
    {
        int length = 2 - binS.length();
        char[] padArray = new char[length];
        Arrays.fill(padArray, '0');
        String padString = new String(padArray);
        return padString + binS;
    }

    static byte[] getSalt()
    {return salt;}
}
