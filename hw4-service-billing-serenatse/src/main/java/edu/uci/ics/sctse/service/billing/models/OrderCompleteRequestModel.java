package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCompleteRequestModel
{
    @JsonProperty(value = "paymentId", required = true)
    private String paymentId;
    @JsonProperty(value = "token", required = true)
    private String token;
    @JsonProperty(value = "PayerID", required = true)
    private String PayerID;

    @JsonCreator
    public OrderCompleteRequestModel() { }

    @JsonCreator
    public OrderCompleteRequestModel(
            @JsonProperty(value = "paymentId", required = true) String paymentId,
            @JsonProperty(value = "token", required = true) String token,
            @JsonProperty(value = "PayerID", required = true) String PayerID)
    {
        this.paymentId = paymentId;
        this.token = token;
        this.PayerID = PayerID;
    }

    @JsonProperty(value = "paymentId")
    public String getPaymentId()
    {
        return paymentId;
    }

    @JsonProperty(value = "token")
    public String getToken()
    {
        return token;
    }

    @JsonProperty(value = "PayerID")
    public String getPayerID()
    {
        return PayerID;
    }
}
