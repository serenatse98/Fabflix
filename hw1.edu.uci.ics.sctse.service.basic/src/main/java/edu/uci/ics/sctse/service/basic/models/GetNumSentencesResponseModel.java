package edu.uci.ics.sctse.service.basic.models;

public class GetNumSentencesResponseModel
{
    int resultCode;
    String message;
    int numRecords;

    public GetNumSentencesResponseModel() { }

    public GetNumSentencesResponseModel(int resultCode, String message, int numRecords)
    {
        this.resultCode = resultCode;
        this.message = message;
        this.numRecords = numRecords;
    }

    public int getResultCode()
    {
        return resultCode;
    }

    public String getMessage()
    {
        return message;
    }

    public int getNumRecords()
    {
        return numRecords;
    }
}
