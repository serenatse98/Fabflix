package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerUpdateRequestModel
{
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "firstName", required = true)
    private String firstName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "ccId", required = true)
    private String ccid;
    @JsonProperty(value = "address", required = true)
    private String address;

    @JsonCreator
    public CustomerUpdateRequestModel() { }

    @JsonCreator

    public CustomerUpdateRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "firstName", required = true) String firstName,
            @JsonProperty(value = "lastName", required = true) String lastName,
            @JsonProperty(value = "ccId", required = true) String ccid,
            @JsonProperty(value = "address", required = true) String address)
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccid = ccid;
        this.address = address;
    }

    @JsonProperty(value = "email")
    public String getEmail()
    {
        return email;
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

    @JsonProperty(value = "ccId")
    public String getCcid()
    {
        return ccid;
    }

    @JsonProperty(value = "address")
    public String getAddress()
    {
        return address;
    }
}
