package edu.uci.ics.sctse.service.billing.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Items
{
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "movieId")
    private String movieId;
    @JsonProperty(value = "quantity")
    private int quantity;

    public Items() { }

    public Items(@JsonProperty(value = "email") String email,
                 @JsonProperty(value = "movieId") String movieId,
                 @JsonProperty(value = "quantity") int quantity)
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
