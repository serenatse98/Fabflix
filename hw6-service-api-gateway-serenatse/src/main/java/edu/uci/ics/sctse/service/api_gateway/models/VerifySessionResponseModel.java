package edu.uci.ics.sctse.service.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "resultCode", "message", "sessionID" })
public class VerifySessionResponseModel
    extends GeneralResponseModel
{
    private String sessionID;

    public VerifySessionResponseModel(
            @JsonProperty(value = "resultCode") int resultCode,
            @JsonProperty(value = "sessionID") String sessionID)
    {
        super(resultCode);
        this.sessionID = sessionID;
    }

    @Override
    public String toString()
    {
        return "RecultCode: " + getResultCode() + ", Message: " + getMessage() + ", SessionID: " + sessionID;
    }

    @JsonProperty(value = "sessionID")
    public String getSessionID()
    {
        return sessionID;
    }
}
