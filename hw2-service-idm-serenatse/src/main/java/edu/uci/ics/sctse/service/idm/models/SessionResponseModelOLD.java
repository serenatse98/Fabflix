package edu.uci.ics.sctse.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.idm.core.Validate;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponseModelOLD
    implements Validate
{
    private int resultCode;
    private String message;
    private String sessionID;

    @JsonCreator
    public SessionResponseModelOLD(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "sessionID") String sessionID)
    {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }


    @Override
    public boolean isValid()
    {
        ServiceLogger.LOGGER.info("Result Code: " + resultCode);
        ServiceLogger.LOGGER.info("Message: " + message);
        ServiceLogger.LOGGER.info("SessionID(optional): " + sessionID);

        return message != null && resultCode != 0;
    }

    @Override
    public String toString()
    {
        return "RegisterResponseModel{" +
                "\nResultCode: " + resultCode +
                "\nMessage: " + message +
                "\nsessionID(optional): " + sessionID + "\n}";
    }

    @JsonProperty("resultCode")
    public int getResultCode()
    {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage()
    {
        return message;
    }

    @JsonProperty("sessionID")
    public String getSessionID()
    {
        return sessionID;
    }
}
