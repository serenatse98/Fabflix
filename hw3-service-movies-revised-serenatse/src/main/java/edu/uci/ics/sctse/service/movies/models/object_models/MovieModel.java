package edu.uci.ics.sctse.service.movies.models.object_models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieModel
{
    private String movieId;
    private String title;
    private String director;
    private int year;
    private float rating;
    private int numVotes;
    private Object hidden;


    private String backdrop_path;
    private Object budget;
    private String overview;
    private String poster_path;
    private Object revenue;
    private GenreModel[] genres;
    private StarModel[] stars;

    @JsonCreator
    public MovieModel(
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "director", required = true) String director,
            @JsonProperty(value = "year", required = true) int year,
            @JsonProperty(value = "rating", required = true) float rating,
            @JsonProperty(value = "numVotes", required = true) int numVotes)
    {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.rating = rating;
        this.numVotes = numVotes;
        this.hidden = null;
    }

    @JsonCreator
    public MovieModel(
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "director", required = true) String director,
            @JsonProperty(value = "year", required = true) int year,
            @JsonProperty(value = "rating", required = true) float rating,
            @JsonProperty(value = "numVotes", required = true) int numVotes,
            @JsonProperty(value = "hidden") boolean hidden)
    {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.rating = rating;
        this.numVotes = numVotes;
        this.hidden = hidden;
    }

    @JsonCreator
    public MovieModel(
            @JsonProperty(value = "movieId", required = true) String movieId,
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "director", required = true) String director,
            @JsonProperty(value = "year") int year,
            @JsonProperty(value = "backdrop_path") String backdrop_path,
            @JsonProperty(value = "budget") Object budget,
            @JsonProperty(value = "overview") String overview,
            @JsonProperty(value = "poster_path") String poster_path,
            @JsonProperty(value = "revenue") Object revenue,
            @JsonProperty(value = "rating", required = true) float rating,
            @JsonProperty(value = "numVotes", required = true) int numVotes,
            @JsonProperty(value = "genres", required = true) GenreModel[] genres,
            @JsonProperty(value = "stars", required = true) StarModel[] stars)
    {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.rating = rating;
        this.numVotes = numVotes;
        this.genres = genres;
        this.stars = stars;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.revenue = revenue;
    }

    @JsonProperty(value = "movieId")
    public String getMovieId()
    {
        return movieId;
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
    public Object getBudget()
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
    public Object getRevenue()
    {
        return revenue;
    }

    @JsonProperty(value = "rating")
    public float getRating()
    {
        return rating;
    }

    @JsonProperty(value = "numVotes")
    public int getNumVotes()
    {
        return numVotes;
    }

    @JsonProperty(value = "hidden")
    public Object getHidden()
    {
        return hidden;
    }

    @JsonProperty(value = "genres")
    public GenreModel[] getGenres()
    {
        return genres;
    }

    @JsonProperty(value = "stars")
    public StarModel[] getStars()
    {
        return stars;
    }
}
