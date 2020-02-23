package edu.uci.ics.sctse.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartInsertRequestModel
{
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "quantity", required = true)
    private int quantity;

    @JsonCreator
    public CartInsertRequestModel() { }

    @JsonCreator
    public CartInsertRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "quantity", required = true) int quantity)
    {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
    }

    @JsonProperty(value = "email")
    public String getEmail()
    {
        return email;
    }

    @JsonProperty(value = "movieId")
    public String getMovieId()
    {
        return movieId;
    }

    @JsonProperty(value = "quantity")
    public int getQuantity()
    {
        return quantity;
    }
}
