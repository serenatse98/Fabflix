package edu.uci.ics.sctse.service.billing.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionFee
{
    @JsonProperty(value = "value")
    private String value;
    @JsonProperty(value = "currency")
    private String currency;

    public TransactionFee(
            @JsonProperty(value = "value") String value,
            @JsonProperty(value = "currency") String currency)
    {
        this.value = value;
        this.currency = currency;
    }

    public String getValue()
    {
        return value;
    }

    public String getCurrency()
    {
        return currency;
    }
}
