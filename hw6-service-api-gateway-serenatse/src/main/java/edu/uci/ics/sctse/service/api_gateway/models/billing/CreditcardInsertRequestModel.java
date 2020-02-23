package edu.uci.ics.sctse.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

import java.sql.Date;

public class CreditcardInsertRequestModel
        extends RequestModel
{
    private String id;
    private String firstName;
    private String lastName;
    private Date expiration;

    @JsonCreator
    public CreditcardInsertRequestModel(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "firstName", required = true) String firstName,
            @JsonProperty(value = "lastName", required = true) String lastName,
            @JsonProperty(value = "expiration", required = true) Date expiration)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiration = expiration;
    }

    @JsonProperty(value = "id")
    public String getId()
    {
        return id;
    }

    @JsonProperty(value = "firstName")
    public String getFirstName()
    {
        return firstName;
    }

    @JsonProperty(value = "lastName")
    public String getLastName()
    {
        return lastName;
    }

    @JsonProperty(value = "expiration")
    public Date getExpiration()
    {
        return expiration;
    }
}

