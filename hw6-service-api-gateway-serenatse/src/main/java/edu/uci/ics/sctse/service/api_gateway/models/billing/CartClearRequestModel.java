package edu.uci.ics.sctse.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class CartClearRequestModel
        extends RequestModel
{
    private String email;

    @JsonCreator
    public CartClearRequestModel(
            @JsonProperty(value = "email", required = true) String email)
    {
        this.email = email;
    }

    @Override
    public String toString() {
        return "CartClearRequestModel [Email: " + email + "]";
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }
}
