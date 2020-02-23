package edu.uci.ics.sctse.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class OrderRetrieveRequestModel
        extends RequestModel
{
    private String email;

    @JsonCreator
    public OrderRetrieveRequestModel(
            @JsonProperty(value = "email", required = true) String email)
    {
        this.email = email;
    }

    @JsonProperty(value = "email")
    public String getEmail()
    {
        return email;
    }
}