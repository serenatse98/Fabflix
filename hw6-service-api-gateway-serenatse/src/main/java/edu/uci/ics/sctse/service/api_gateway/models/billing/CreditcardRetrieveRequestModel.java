package edu.uci.ics.sctse.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class CreditcardRetrieveRequestModel
        extends RequestModel
{
    private String id;

    @JsonCreator
    public CreditcardRetrieveRequestModel(
            @JsonProperty(value = "id", required = true) String id)
    {
        this.id = id;
    }

    @JsonProperty(value = "id")
    public String getId()
    {
        return id;
    }
}
