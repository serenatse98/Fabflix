package edu.uci.ics.sctse.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddStarRequestModel
    extends RequestModel
{
    private String name;
    private int birthYear;

    @JsonCreator
    public AddStarRequestModel(
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "birthYear") int birthYear)
    {
        this.name = name;
        this.birthYear = birthYear;
    }

    @JsonProperty(value = "name")
    public String getName()
    {
        return name;
    }

    @JsonProperty(value = "birthYear")
    public int getBirthYear()
    {
        return birthYear;
    }
}
