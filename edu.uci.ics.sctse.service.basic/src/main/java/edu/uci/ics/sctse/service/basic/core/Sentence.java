package edu.uci.ics.sctse.service.basic.core;

public class Sentence
{
    private int id;
    private String sentence;
    private int len;

    public Sentence() {};

    public Sentence(int id, String sentence, int len)
    {
        this.id = id;
        this.sentence = sentence;
        this.len = len;
    }

    public String toString()
    {
        return "ID: " + id + "; Sentence: " + sentence + "; Length: " + len;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
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
