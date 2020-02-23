package edu.uci.ics.sctse.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponseModel
{
    private int resultCode;
    private String message;
    private String sessionID;

    @JsonCreator
    public SessionResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "sessionID") String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    @JsonProperty(value = "resultCode")
    public int getResultCode()
    {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage()
    {
        return message;
    }

    @JsonProperty(value = "sessionID")
    public String getSessionID()
    {
        return sessionID;
    }
}
