package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.uci.ics.sctse.service.movies.models.object_models.StarModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "resultCode", "message", "stars" })
public class StarIDResponseModel
        extends GeneralResponseModel
{
    private StarModel star;

    @JsonCreator
    public StarIDResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "stars", required = true) StarModel star)
    {
        super(resultCode);
        this.star = star;
    }

    @JsonProperty(value = "stars")
    public StarModel getStar()
    {
        return star;
    }
}
