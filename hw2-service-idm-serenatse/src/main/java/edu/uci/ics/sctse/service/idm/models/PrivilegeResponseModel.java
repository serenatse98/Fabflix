package edu.uci.ics.sctse.service.idm.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;


public class PrivilegeResponseModel
{
    private int resultCode;
    private String message;

    @JsonCreator
    public PrivilegeResponseModel(
            @JsonProperty(value = "resultCode", required = true) int resultCode,
            @JsonProperty(value = "message", required = true) String message)
    {
        this.resultCode = resultCode;
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "PrivilegeResponseModel{" +
                "\nResultCode: " + resultCode +
                "\nMessage: " + message + "\n}";
    }

    @JsonProperty
    public int getResultCode()
    {
        return resultCode;
    }

    @JsonProperty
    public String getMessage()
    {
        return message;
    }
}
