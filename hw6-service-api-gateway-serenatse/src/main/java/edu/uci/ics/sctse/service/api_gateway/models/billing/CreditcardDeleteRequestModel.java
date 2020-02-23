package edu.uci.ics.sctse.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;


public class CreditcardDeleteRequestModel
        extends RequestModel
{
    private String id;

    @JsonCreator
    public CreditcardDeleteRequestModel(
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
