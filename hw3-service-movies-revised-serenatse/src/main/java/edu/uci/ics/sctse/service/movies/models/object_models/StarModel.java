package edu.uci.ics.sctse.service.movies.models.object_models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarModel
{
    private String id;
    private String name;
    private Object birthYear;

    @JsonCreator
    public StarModel(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty (value = "birthYear") Object birthYear)
    {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    @JsonProperty(value = "id")
    public String getId()
    {
        return id;
    }

    @JsonProperty(value = "name")
    public String getName()
    {
        return name;
    }

    @JsonProperty (value = "birthYear")
    public Object getBirthYear()
    {
        return birthYear;
    }
}
