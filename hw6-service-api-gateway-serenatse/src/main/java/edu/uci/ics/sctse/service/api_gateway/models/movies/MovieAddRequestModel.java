package edu.uci.ics.sctse.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;
import edu.uci.ics.sctse.service.api_gateway.utilities.movie_models.GenreModel;

public class MovieAddRequestModel
    extends RequestModel
{
    private String title;
    private String director;
    private int year;
    private String backdrop_path;
    private int budget;
    private String overview;
    private String poster_path;
    private int revenue;
    private GenreModel[] Genres;

    @JsonCreator
    public MovieAddRequestModel(
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "director", required = true) String director,
            @JsonProperty(value = "year", required = true) int year,
            @JsonProperty(value = "backdrop_path") String backdrop_path,
            @JsonProperty(value = "budget") int budget,
            @JsonProperty(value = "overview") String overview,
            @JsonProperty(value = "poster_path") String poster_path,
            @JsonProperty(value = "revenue") int revenue,
            @JsonProperty(value = "genres", required = true) GenreModel[] Genres)
    {
        this.title = title;
        this.director = director;
        this.year = year;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.revenue = revenue;
        this.Genres = Genres;
    }

    @JsonProperty(value = "title")
    public String getTitle()
    {
        return title;
    }

    @JsonProperty(value = "director")
    public String getDirector()
    {
        return director;
    }

    @JsonProperty(value = "year")
    public int getYear()
    {
        return year;
    }

    @JsonProperty(value = "backdrop_path")
    public String getBackdrop_path()
    {
        return backdrop_path;
    }

    @JsonProperty(value = "budget")
    public int getBudget()
    {
        return budget;
    }

    @JsonProperty(value = "overview")
    public String getOverview()
    {
        return overview;
    }

    @JsonProperty(value = "poster_path")
    public String getPoster_path()
    {
        return poster_path;
    }

    @JsonProperty(value = "revenue")
    public int getRevenue()
    {
        return revenue;
    }

    @JsonProperty(value = "genres", required = true)
    public GenreModel[] getGenres()
    {
        return Genres;
    }
}
