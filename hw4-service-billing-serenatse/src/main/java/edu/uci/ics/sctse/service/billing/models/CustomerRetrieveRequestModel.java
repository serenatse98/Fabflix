package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerRetrieveRequestModel
{
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonCreator
    public CustomerRetrieveRequestModel() { }

    @JsonCreator
    public CustomerRetrieveRequestModel(@JsonProperty(value = "email", required = true) String email)
    {
        this.email = email;
    }

    @JsonProperty(value = "email")
    public String getEmail()
    {
        return email;
    }
}
