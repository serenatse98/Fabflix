package edu.uci.ics.sctse.service.basic.models;

public class GetSentenceFromIDResponseModel
{
    int resultCode;
    String message;
    Object record;

    public GetSentenceFromIDResponseModel() { }

    public GetSentenceFromIDResponseModel(int resultCode, String message, Object record)
    {
        this.resultCode = resultCode;
        this.message = message;
        this.record = record;
    }

    public int getResultCode()
    {
        return resultCode;
    }

    public String getMessage()
    {
        return message;
    }

    public Object getRecord()
    {
        return record;
    }
}
