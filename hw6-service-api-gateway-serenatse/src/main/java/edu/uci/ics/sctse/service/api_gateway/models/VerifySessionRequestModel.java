package edu.uci.ics.sctse.service.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifySessionRequestModel
{
    private String email;
    private String sessionID;

    @JsonCreator
    public VerifySessionRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "sessionID", required = true) String sessionID)
    {
        this.email = email;
        this.sessionID = sessionID;
    }

    @Override
    public String toString() {
        return "Email: " + email + ", SessionID: " + sessionID;
    }

    @JsonProperty("sessionID")
    public String getSessionID() {
        return sessionID;
    }

    @JsonProperty(value = "email")
    public String getEmail()
    {
        return email;
    }
}