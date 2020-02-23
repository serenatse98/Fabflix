package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.billing.objects.Customer;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerRetrieveResponseModel
    extends GeneralResponseModel
{
    @JsonProperty(value = "customer")
    private Customer customer;

    @JsonCreator
    public CustomerRetrieveResponseModel() { }

    @JsonCreator
    public CustomerRetrieveResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "customer") Customer customer)
    {
        super(resultCode, message);
        this.customer = customer;
    }

    @JsonProperty(value = "customer")
    public Customer getCustomer()
    {
        return customer;
    }
}
