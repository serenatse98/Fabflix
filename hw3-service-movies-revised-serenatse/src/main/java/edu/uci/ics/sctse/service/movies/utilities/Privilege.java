package edu.uci.ics.sctse.service.movies.utilities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Privilege
{
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonCreator
    public Privilege(@JsonProperty(value = "resultCode", required = true) int resultCode,
                     @JsonProperty(value = "message", required = true) String message)
    {
        this.resultCode = resultCode;
        this.message = message;
    }

    @JsonProperty(value = "resultCode")
    public int getResultCode()
    {
        return resultCode;
    }

    @JsonProperty(value = "message")
    public String getMessage()
    {
        return message;
    }
}
