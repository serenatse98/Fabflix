package edu.uci.ics.sctse.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class CartDeleteRequestModel
        extends RequestModel
{
    private String email;
    private String movieId;

    @JsonCreator
    public CartDeleteRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "movieId", required = true) String movieId)
    {
        this.email = email;
        this.movieId = movieId;
    }

    @Override
    public String toString() {
        return "CartDeleteRequestModel [Email: " + email + ", movieID: " + movieId + "]";
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
}
