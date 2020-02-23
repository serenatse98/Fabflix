package edu.uci.ics.sctse.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class StarSearchRequestModel
    extends RequestModel
{
    private String name;
    private int birthYear;
    private String movieTitle;
    private int offset;
    private int limit;
    private String orderby;
    private String direction;

    @JsonCreator
    public StarSearchRequestModel(
            @JsonProperty(value = "name") String name,
            @JsonProperty(value = "birthYear") int birthYear,
            @JsonProperty(value = "movieTitle") String movieTitle,
            @JsonProperty(value = "offset") int offset,
            @JsonProperty(value = "limit") int limit,
            @JsonProperty(value = "orderby") String orderby,
            @JsonProperty(value = "direction") String direction)
    {
        this.name = name;
        this.birthYear = birthYear;
        this.movieTitle = movieTitle;
        this.offset = offset;
        this.limit = limit;
        this.orderby = orderby;
        this.direction = direction;
    }

    @JsonProperty(value = "name")
    public String getName()
    {
        return name;
    }

    @JsonProperty(value = "birthYear")
    public int getBirthYear()
    {
        return birthYear;
    }

    @JsonProperty(value = "movieTitle")
    public String getMovieTitle()
    {
        return movieTitle;
    }

    @JsonProperty(value = "offset", required = true)
    public int getOffset()
    {
        return offset;
    }

    @JsonProperty(value = "limit", required = true)
    public int getLimit()
    {
        return limit;
    }

    @JsonProperty(value = "direction")
    public String getDirection()
    {
        return direction;
    }

    @JsonProperty(value = "orderby", required = true)
    public String getOrderby()
    {
        return orderby;
    }
}
