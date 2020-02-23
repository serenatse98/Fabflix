package edu.uci.ics.sctse.service.billing.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction
{
    @JsonProperty(value = "transactionId")
    private String transactionId;
    @JsonProperty(value = "state")
    private String state;
    @JsonProperty(value = "amount")
    private Amount amount;
    @JsonProperty(value = "transaction_fee")
    private TransactionFee transaction_fee;
    @JsonProperty(value = "create_time")
    private String create_time;
    @JsonProperty(value = "update_time")
    private String update_time;
    @JsonProperty(value = "items")
    private Order[] items;

    public Transaction(
            @JsonProperty(value = "transactionId") String transactionId,
            @JsonProperty(value = "state") String state,
            @JsonProperty(value = "amount") Amount amount,
            @JsonProperty(value = "transaction_fee") TransactionFee transaction_fee,
            @JsonProperty(value = "create_time") String create_time,
            @JsonProperty(value = "update_time") String update_time,
            @JsonProperty(value = "items") Order[] items)
    {
        this.transactionId = transactionId;
        this.state = state;
        this.amount = amount;
        this.transaction_fee = transaction_fee;
        this.create_time = create_time;
        this.update_time = update_time;
        this.items = items;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public String getState()
    {
        return state;
    }

    public Amount getAmount()
    {
        return amount;
    }

    public TransactionFee getTransaction_fee()
    {
        return transaction_fee;
    }

    public String getCreate_time()
    {
        return create_time;
    }

    public String getUpdate_time()
    {
        return update_time;
    }

    public Order[] getItems()
    {
        return items;
    }
}
