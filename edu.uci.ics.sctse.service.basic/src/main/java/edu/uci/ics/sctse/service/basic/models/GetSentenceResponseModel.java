package edu.uci.ics.sctse.service.basic.models;

import edu.uci.ics.sctse.service.basic.core.Sentence;
import edu.uci.ics.sctse.service.basic.core.Validate;
import edu.uci.ics.sctse.service.basic.logger.ServiceLogger;

import java.util.ArrayList;

public class GetSentenceResponseModel
    implements Validate
{
    private SentenceModel[] sentences;

    public GetSentenceResponseModel() { }

    private GetSentenceResponseModel(SentenceModel[] sentences)
    {
        this.sentences = sentences;
    }

    public static GetSentenceResponseModel buildModelFromList(ArrayList<Sentence> sentences)
    {
        ServiceLogger.LOGGER.info("Creating model...");

        if (sentences == null)
        {
            ServiceLogger.LOGGER.info("No sentence list passes to model constructor");
            return new GetSentenceResponseModel(null);
        }

        ServiceLogger.LOGGER.info("Sentence list is not empty...");
        int len = sentences.size();
        SentenceModel[] array = new SentenceModel[len];

        for (int i = 0; i < len; ++i)
        {
            ServiceLogger.LOGGER.info("Adding sentence #" + sentences.get(i).getId() + " to array.");
            SentenceModel sm = SentenceModel.buildModelFromObject(sentences.get(i));

            if (sm.isValid())
            {
                array[i] = sm;
            }
        }
        ServiceLogger.LOGGER.info("Finished building model. Sentences array contains: ");
        for (SentenceModel sm : array)
        {
            ServiceLogger.LOGGER.info("\t" + sm);
        }

        return new GetSentenceResponseModel(array);
    }


    public int getNumberOfSentences() { return sentences.length; }

    public SentenceModel getSingleSentence()
    {
        return sentences[0];
    }

    @Override
    public boolean isValid()
    {
        ServiceLogger.LOGGER.info("sentences == null? " + (sentences == null));
        ServiceLogger.LOGGER.info("sentences.length > 0? " + (sentences.length > 0));
        return (sentences != null) || (sentences.length == 0);
    }
}
