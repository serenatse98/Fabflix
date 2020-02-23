package edu.uci.ics.sctse.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class SearchMovieRequestModel
    extends RequestModel
{
    private String title;
    private String genre;
    private int year;
    private String director;
    private boolean hidden;
    private int offset;
    private int limit;
    private String direction;
    private String orderby;

    @JsonCreator
    public SearchMovieRequestModel(
          @JsonProperty(value = "title") String title,
          @JsonProperty(value = "genre") String genre,
          @JsonProperty(value = "year") int year,
          @JsonProperty(value = "director") String director,
          @JsonProperty(value = "hidden") boolean hidden,
          @JsonProperty(value = "limit") int limit,
          @JsonProperty(value = "offset") int offset,
          @JsonProperty(value = "orderby") String orderby,
          @JsonProperty(value = "direction") String direction)
    {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.director = director;
        this.hidden = hidden;
        this.offset = offset;
        this.limit = limit;
        this.direction = direction;
        this.orderby = orderby;
    }

    @JsonProperty(value = "title")
    public String getTitle()
    {
        return title;
    }

    @JsonProperty(value = "genre")
    public String getGenre()
    {
        return genre;
    }

    @JsonProperty(value = "year")
    public int getYear()
    {
        return year;
    }

    @JsonProperty(value = "director")
    public String getDirector()
    {
        return director;
    }

    @JsonProperty(value = "hidden")
    public boolean isHidden()
    {
        return hidden;
    }

    @JsonProperty(value = "offset")
    public int getOffset()
    {
        return offset;
    }

    @JsonProperty(value = "limit")
    public int getLimit()
    {
        return limit;
    }

    @JsonProperty(value = "direction")
    public String getDirection()
    {
        return direction;
    }

    @JsonProperty(value = "orderby")
    public String getOrderby()
    {
        return orderby;
    }
}
