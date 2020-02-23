package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.billing.objects.Order;
import edu.uci.ics.sctse.service.billing.objects.Transaction;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRetrieveResponseModel
    extends GeneralResponseModel
{
    @JsonProperty(value = "transactions")
    private ArrayList<Transaction> transactions;

    @JsonCreator
    public OrderRetrieveResponseModel() { }

    @JsonCreator
    public OrderRetrieveResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "transactions") ArrayList<Transaction> transactions)
    {
        super(resultCode, message);
        this.transactions = transactions;
    }


    public ArrayList<Transaction> getTransactions()
    {
        return transactions;
    }
}
