package edu.uci.ics.sctse.service.api_gateway.utilities.movie_models;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;

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

    public GenreModel() { }

    public GenreModel(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "{\"id\":" + id + ", \"name\":\"" + name + "\"}";
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}
