package edu.uci.ics.sctse.service.billing.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer
{
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "firstName")
    private String firstName;
    @JsonProperty(value = "lastName")
    private String lastName;
    @JsonProperty(value = "ccId")
    private String ccId;
    @JsonProperty(value = "address")
    private String address;

    public Customer() { }

    public Customer(String email, String firstName, String lastName, String ccId, String address)
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ccId = ccId;
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
        return ccId;
    }

    @JsonProperty(value = "address")
    public String getAddress()
    {
        return address;
    }
}
