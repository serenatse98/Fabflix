package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditcardDeleteRequestModel
{
    @JsonProperty(value = "id", required = true)
    private String id;

    @JsonCreator
    public CreditcardDeleteRequestModel() { }

    @JsonCreator
    public CreditcardDeleteRequestModel(@JsonProperty(value = "id", required = true) String id)
    {
        this.id = id;
    }

    @JsonProperty(value = "id")
    public String getId()
    {
        return id;
    }
}
