package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.sctse.service.movies.models.object_models.StarModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "resultCode", "message", "stars" })
public class StarResponseModel
    extends GeneralResponseModel
{
    private StarModel[] stars;

    @JsonCreator
    public StarResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "stars", required = true) StarModel[] stars)
    {
        super(resultCode);
        this.stars = stars;
    }

    @JsonProperty(value = "stars")
    public StarModel[] getStars()
    {
        return stars;
    }
}
