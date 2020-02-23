package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.sctse.service.movies.models.object_models.MovieModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "resultCode", "message", "movies" })
public class SearchResponseModel
    extends GeneralResponseModel
{
    private MovieModel[] movies;

    @JsonCreator
    public SearchResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "movies", required = true) MovieModel[] movies)
    {
        super(resultCode);
        this.movies = movies;
    }

    private MovieModel movie;
    @JsonCreator
    public SearchResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "movie", required = true) MovieModel movie)
    {
        super(resultCode);
        this.movie = movie;
    }

    @JsonProperty(value = "movies")
    public MovieModel[] getMovies()
    {
        return movies;
    }

    @JsonProperty(value = "movie")
    public MovieModel getMovie()
    {
        return movie;
    }
}
