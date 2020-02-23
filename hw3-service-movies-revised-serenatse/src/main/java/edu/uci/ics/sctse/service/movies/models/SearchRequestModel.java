package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchRequestModel
    extends RequestModel
{
    private String title;
    private String genre;
    private int year;
    private String director;
    private boolean hidden;
    private int limit;
    private int offset;
    private String orderby;
    private String direction;

    @JsonCreator
    public SearchRequestModel(
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
        this.orderby = orderby;
        this.direction = direction;
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

