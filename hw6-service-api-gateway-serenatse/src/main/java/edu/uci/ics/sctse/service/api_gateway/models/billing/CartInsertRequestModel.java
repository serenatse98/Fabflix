package edu.uci.ics.sctse.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class CartInsertRequestModel
    extends RequestModel
{
    private String email;
    private String movieId;
    private int quantity;

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

    @Override
    public String toString() {
        return "CartInsertRequestModel [Email: " + email + ", movieID: " + movieId + ", quantity: " + quantity + "]";
    }

    @JsonProperty("email")
    public String getEmail() {
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
