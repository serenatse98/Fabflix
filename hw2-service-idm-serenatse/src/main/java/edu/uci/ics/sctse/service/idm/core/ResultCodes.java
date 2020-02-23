package edu.uci.ics.sctse.service.idm.core;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


public class ResultCodes
{
    private int c;

    public ResultCodes() { }

    public ResultCodes(int c)
    {
        this.c = c;
    }

    public String getResponse(int c)
    {
        String result = "";
        switch (c)
        {
            case -14:
            case -12:
            case -13:
            case -11:
            case -10:
            case -3:
            case -2:
                result = "bad";
                break;

            case -1:
                result = "error";
                break;

            case 11:
            case 12:
            case 13:
            case 14:
            case 16:
            case 110:
            case 120:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 140:
            case 141:
                result = "ok";
                break;
        }

        return result;
    }

    static public String resultMessage(int c)
    {
        String message = "";
        switch (c)
        {
            // 400 BAD REQUEST
            case -14:
                message = "Privilege level out of valid range.";
                break;
            case -13:
                message = "Token has invalid length.";
                break;
            case -12:
                message = "Password has invalid length (cannot be empty/null).";
                break;
            case -11:
                message = "Email address has invalid format.";
                break;
            case -10:
                message = "Email address has invalid length.";
                break;
            case -3:
                message = "JSON Parse Exception.";
                break;
            case -2:
                message = "JSON Mapping Exception.";
                break;

            // 500 INTERNAL SERVER ERROR
            case -1:
                message = "Internal Server Error.";
                break;

            // 200 OK
            case 11:
                message = "Passwords do not match.";
                break;
            case 12:
                message = "Password does not meet length requirements.";
                break;
            case 13:
                message = "Password does not meet character requirements.";
                break;
            case 14:
                message = "User not found.";
                break;
            case 16:
                message = "Email already in use.";
                break;
            case 110:
                message = "User registered successfully.";
                break;
            case 120:
                message = "User logged in successfully.";
                break;
            case 130:
                message = "Session is active.";
                break;
            case 131:
                message = "Session is expired.";
                break;
            case 132:
                message = "Session is closed.";
                break;
            case 133:
                message = "Session is revoked.";
                break;
            case 134:
                message = "Session not found.";
                break;
            case 140:
                message = "User has sufficient privilege level.";
                break;
            case 141:
                message = "User has insufficient privilege level.";
                break;
        }

        return message;
    }
}
