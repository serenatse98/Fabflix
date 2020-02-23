package edu.uci.ics.sctse.service.basic.models;

public class ValidStringResponseModel
{
    int resultCode;
    String message;

    public ValidStringResponseModel() { }

    public ValidStringResponseModel(int responseCode, String message)
    {
        this.resultCode = responseCode;
        this.message = message;
    }

    public int getResultCode()
    {
        return resultCode;
    }

    public String getMessage()
    {
        return message;
    }
}
