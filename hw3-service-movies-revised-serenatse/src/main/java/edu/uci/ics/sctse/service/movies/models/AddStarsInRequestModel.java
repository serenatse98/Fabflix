package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddStarsInRequestModel
    extends RequestModel
{
    private String starid;
    private String movieid;

    @JsonCreator
    public AddStarsInRequestModel(
            @JsonProperty(value = "starid", required = true) String starid,
            @JsonProperty(value = "movieid", required = true) String movieid)
    {
        this.starid = starid;
        this.movieid = movieid;
    }

    @JsonProperty(value = "starid")
    public String getStarid()
    {
        return starid;
    }

    @JsonProperty(value = "movieid")
    public String getMovieid()
    {
        return movieid;
    }
}
