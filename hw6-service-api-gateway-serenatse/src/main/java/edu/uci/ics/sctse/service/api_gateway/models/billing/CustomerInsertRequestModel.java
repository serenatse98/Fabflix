package edu.uci.ics.sctse.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class CustomerInsertRequestModel
    extends RequestModel
{
    private String email;
    private String firstName;
    private String lastName;
    private String ccid;
    private String address;

    @JsonCreator
    public CustomerInsertRequestModel(
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
