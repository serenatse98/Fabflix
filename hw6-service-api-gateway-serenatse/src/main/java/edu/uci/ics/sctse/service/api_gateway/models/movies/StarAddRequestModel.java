package edu.uci.ics.sctse.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.api_gateway.models.RequestModel;

public class StarAddRequestModel
        extends RequestModel
{
    private String name;
    private int birthYear;

    @JsonCreator
    public StarAddRequestModel(
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
