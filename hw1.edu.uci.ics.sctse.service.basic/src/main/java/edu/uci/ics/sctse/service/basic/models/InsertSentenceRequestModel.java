package edu.uci.ics.sctse.service.basic.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.sctse.service.basic.core.Validate;

public class InsertSentenceRequestModel
    implements Validate
{
//    private int id;
    private String sentence;
    private int len;

    public InsertSentenceRequestModel() { }

    public InsertSentenceRequestModel(
            @JsonProperty(value = "input", required = true) String sentence,
            @JsonProperty(value = "len", required = true) int len)
    {
        this.sentence = sentence;
        this.len = len;
    }

    @Override
    public boolean isValid()
    {
        int sentenceLen = sentence.length();

        return (sentenceLen > 0 && sentenceLen <= 512) && (len > 0);
    }

    public String getSentence()
    {
        return sentence;
    }

    public void setSentence(String sentence)
    {
        this.sentence = sentence;
    }

    public int getLen()
    {
        return len;
    }

    public void setLen(int len)
    {
        this.len = len;
    }
}
