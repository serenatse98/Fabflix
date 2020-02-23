package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingRequestModel
    extends RequestModel
{
    private String id;
    private float rating;

    @JsonCreator
    public RatingRequestModel(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "rating", required = true) float rating)
    {
        this.id = id;
        this.rating = rating;
    }

    @JsonProperty(value = "id")
    public String getId()
    {
        return id;
    }

    @JsonProperty(value = "rating")
    public float getRating()
    {
        return rating;
    }
}
