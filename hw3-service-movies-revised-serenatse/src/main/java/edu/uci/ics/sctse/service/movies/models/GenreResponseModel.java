package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.sctse.service.movies.models.object_models.GenreModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "resultCode", "message", "genres" })
public class GenreResponseModel
    extends GeneralResponseModel
{
    private GenreModel[] genres;

    @JsonCreator
    public GenreResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "genres") GenreModel[] genres)
    {
        super(resultCode);
        this.genres = genres;
    }

    @JsonProperty(value = "genres")
    public GenreModel[] getGenres()
    {
        return genres;
    }
}
