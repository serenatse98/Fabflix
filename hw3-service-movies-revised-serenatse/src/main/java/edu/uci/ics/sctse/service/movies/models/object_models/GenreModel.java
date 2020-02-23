package edu.uci.ics.sctse.service.movies.models.object_models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.movies.MovieService;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreModel
{
    private int id;
    private String name;

    @JsonCreator
    public GenreModel(
            @JsonProperty(value = "id", required = true) int id,
            @JsonProperty(value = "name", required = true) String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "{\"id\":" + id + ", \"name\":\"" + name + "\"}";
    }

    @JsonProperty(value = "id")
    public int getId()
    {
        return id;
    }

    @JsonProperty(value = "name")
    public String getName()
    {
        return name;
    }
}
