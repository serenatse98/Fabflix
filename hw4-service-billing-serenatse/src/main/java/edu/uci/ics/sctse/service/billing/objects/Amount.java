package edu.uci.ics.sctse.service.billing.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Amount
{
    @JsonProperty(value = "total")
    private String total;
    @JsonProperty(value = "currency")
    private String currency;

    public Amount(
            @JsonProperty(value = "total") String total,
            @JsonProperty(value = "currency") String currency)
    {
        this.total = total;
        this.currency = currency;
    }

    public String getTotal()
    {
        return total;
    }

    public String getCurrency()
    {
        return currency;
    }
}
