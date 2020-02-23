package edu.uci.ics.sctse.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.idm.core.Validate;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;

public class RegisterResponseModel
    implements Validate
{
    private int resultCode;
    private String message;

    @JsonCreator
    public RegisterResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message)
    {
        this.resultCode = resultCode;
        this.message = message;
    }

    @Override
    public boolean isValid()
    {
        ServiceLogger.LOGGER.info("Result Code: " + resultCode);
        ServiceLogger.LOGGER.info("Message: " + message);

        return message != null && resultCode != 0;
    }

    @Override
    public String toString()
    {
        return "RegisterResponseModel{" +
                "\nResultCode: " + resultCode +
                "\nMessage: " + message + "\n}";
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
}
