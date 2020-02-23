package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetPriceResponseModel
    extends GeneralResponseModel
{
    private float price;
    private float discount;
    private String movieId;

    @JsonCreator
    public GetPriceResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "price", required = true) float price,
            @JsonProperty(value = "discount", required = true) float discount)
    {
        super(resultCode, message);
        this.movieId = movieId;
        this.price = price;
        this.discount = discount;
    }

    @JsonProperty(value = "movieId")
    public String getMovieId()
    {
        return movieId;
    }

    @JsonProperty(value = "price")
    public float getPrice()
    {
        return price;
    }

    @JsonProperty(value = "discount")
    public float getDiscount()
    {
        return discount;
    }
}
