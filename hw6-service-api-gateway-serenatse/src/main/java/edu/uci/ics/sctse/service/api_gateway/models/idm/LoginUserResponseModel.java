package edu.uci.ics.sctse.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.GeneralResponseModel;

@JsonIgnoreProperties(value = { "dataValid" })
public class LoginUserResponseModel
    extends GeneralResponseModel
{
    private String sessionID;

    @JsonCreator
    public LoginUserResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "sessionID") String sessionID)
    {
        super(resultCode);
        this.sessionID = sessionID;
    }

    @JsonProperty(value = "sessionID")
    public String getSessionID()
    {
        return sessionID;
    }
}
