package edu.uci.ics.sctse.service.basic.models;

import edu.uci.ics.sctse.service.basic.core.Sentence;
import edu.uci.ics.sctse.service.basic.core.Validate;

public class SentenceModel
    implements Validate
{
    private int id;
    private String sentence;
    private int len;

    private SentenceModel() { }

    private SentenceModel(int id, String sentence, int len)
    {
        this.id = id;
        this.sentence = sentence;
        this.len = len;
    }

    public static SentenceModel buildModelFromObject(Sentence s)
    {
        return new SentenceModel(s.getId(), s.getSentence(), s.getLen());
    }

    public String toString()
    {
        return "ID: " + id + "; Sentence: " + sentence + "; Length: " + len;
    }

    public boolean isValid() {
        return (id > 0) && (sentence != null) && (len > 0);
    }

    public int getId()
    {
        return id;
    }

    public String getSentence()
    {
        return sentence;
    }

    public int getLen()
    {
        return len;
    }
}
