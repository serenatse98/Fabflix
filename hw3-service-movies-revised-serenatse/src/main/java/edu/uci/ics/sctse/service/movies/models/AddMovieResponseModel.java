package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "resultCode", "message", "movieid", "genreid" })
public class AddMovieResponseModel
    extends GeneralResponseModel
{
    private String movieid;
    private int[] genreid;

    @JsonCreator
    public AddMovieResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "movieid", required = true) String movieid,
            @JsonProperty(value = "genreid", required = true) int[] genreid)
    {
        super(resultCode);
        this.movieid = movieid;
        this.genreid = genreid;
    }

    @JsonProperty(value = "movieid")
    public String getMovieid()
    {
        return movieid;
    }

    @JsonProperty(value = "genreid")
    public int[] getGenreid()
    {
        return genreid;
    }
}
