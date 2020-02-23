package edu.uci.ics.sctse.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class GenreAddRequestModel
    extends RequestModel
{
    private String name;

    @JsonCreator
    public GenreAddRequestModel(@JsonProperty(value = "name", required = true) String name)
    {
        this.name = name;
    }

    @JsonProperty(value = "name", required = true)
    public String getName()
    {
        return name;
    }
}
