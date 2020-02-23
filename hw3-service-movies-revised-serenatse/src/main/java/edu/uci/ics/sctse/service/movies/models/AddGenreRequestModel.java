package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddGenreRequestModel
    extends RequestModel
{
    private String name;

    @JsonCreator
    public AddGenreRequestModel(@JsonProperty(value = "name", required = true) String name)
    {
        this.name = name;
    }

    @JsonProperty(value = "name")
    public String getName()
    {
        return name;
    }
}
