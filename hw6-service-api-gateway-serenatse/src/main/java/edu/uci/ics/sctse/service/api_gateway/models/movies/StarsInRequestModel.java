package edu.uci.ics.sctse.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class StarsInRequestModel
        extends RequestModel
{
    private String starid;
    private String movieid;

    @JsonCreator
    public StarsInRequestModel(
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
