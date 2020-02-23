package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditcardInsertRequestModel
{
    @JsonProperty(value = "id", required = true)
    private String id;
    @JsonProperty(value = "firstName", required = true)
    private String firstName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "expiration", required = true)
    private Date expiration;

    @JsonCreator
    public CreditcardInsertRequestModel() {}

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
