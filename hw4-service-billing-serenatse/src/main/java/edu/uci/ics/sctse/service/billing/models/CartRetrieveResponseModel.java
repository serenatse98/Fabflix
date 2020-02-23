package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.billing.objects.Items;

public class CartRetrieveResponseModel
    extends GeneralResponseModel
{
    @JsonProperty(value = "items")
    private Items[] items;

    @JsonCreator
    CartRetrieveResponseModel() { }

    @JsonCreator
    public CartRetrieveResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "items") Items[] items)
    {
        super(resultCode, message);
        this.items = items;
    }

    public Items[] getItems()
    {
        return items;
    }
}
