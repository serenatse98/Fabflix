package edu.uci.ics.sctse.service.billing.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;

public class Order
{
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "movieId")
    private String movieId;
    @JsonProperty(value = "quantity")
    private int quantity;
    @JsonProperty(value = "unit_price")
    private float unit_price;
    @JsonProperty(value = "discount")
    private float discount;
    @JsonProperty(value = "saleDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date saleDate;

    public Order() { }

    public Order(@JsonProperty(value = "email") String email,
                 @JsonProperty(value = "movieId") String movieId,
                 @JsonProperty(value = "quantity") int quantity,
                 @JsonProperty(value = "unit_price") float unit_price,
                 @JsonProperty(value = "discount") float discount,
                 @JsonProperty(value = "saleDate") Date saleDate)
    {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.discount = discount;
        this.saleDate = saleDate;
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

    @JsonProperty(value = "unit_price")
    public float getUnit_price()
    {
        return unit_price;
    }

    @JsonProperty(value = "discount")
    public float getDiscount()
    {
        return discount;
    }

    @JsonProperty(value = "saleDate")
    public Date getSaleDate()
    {
        return saleDate;
    }
}
