package edu.uci.ics.sctse.service.basic.models;

public class ValidSentenceRequestModel
{
    String input;
    int len;

    public ValidSentenceRequestModel() { }

    public ValidSentenceRequestModel(String input, int len)
    {
        this.input = input;
        this.len = len;
    }

    public String getInput()
    {
        return input;
    }

    public int getLen()
    {
        return len;
    }
}
