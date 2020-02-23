package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.billing.objects.Creditcard;

public class CreditcardRetrieveResponseModel
    extends GeneralResponseModel
{
    @JsonProperty(value = "creditcard", required = true)
    Creditcard creditcard;

    @JsonCreator
    public CreditcardRetrieveResponseModel() { }

    @JsonCreator
    public CreditcardRetrieveResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "creditcard", required = true) Creditcard creditcard)
    {
        super(resultCode, message);
        this.creditcard = creditcard;
    }

    @JsonProperty(value = "creditcard")
    public Creditcard getCreditcard()
    {
        return creditcard;
    }
}
