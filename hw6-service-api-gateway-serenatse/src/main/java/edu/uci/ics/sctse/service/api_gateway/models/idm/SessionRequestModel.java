package edu.uci.ics.sctse.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class SessionRequestModel
    extends RequestModel
{
    private String email;
    private String sessionID;

    @JsonCreator
    public SessionRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "sessionID", required = true) String sessionID) {
        this.email = email;
        this.sessionID = sessionID;
    }

    @Override
    public String toString() {
        return "SessionRequestModel [Email: " + email + ", sessionID: " + sessionID + "]";
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty(value = "sessionID")
    public String getSessionID()
    {
        return sessionID;
    }
}
