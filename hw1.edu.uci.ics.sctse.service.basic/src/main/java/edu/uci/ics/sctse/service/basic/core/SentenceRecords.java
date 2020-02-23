package edu.uci.ics.sctse.service.basic.core;

import edu.uci.ics.sctse.service.basic.BasicService;
import edu.uci.ics.sctse.service.basic.logger.ServiceLogger;
import edu.uci.ics.sctse.service.basic.models.GetSentenceResponseModel;
import edu.uci.ics.sctse.service.basic.models.InsertSentenceRequestModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SentenceRecords
{
    private static ArrayList<Sentence> addToArray(ArrayList<Sentence> sentences, ResultSet rs)
    {
        try
        {
            while (rs.next())
            {
                Sentence s = new Sentence(
                        rs.getInt("id"),
                        rs.getString("sentence"),
                        rs.getInt("length")
                );
                ServiceLogger.LOGGER.info("Retrieved sentence " + s);
                sentences.add(s);
            }
        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Result Set Failed.");
            e.printStackTrace();
        }
        return sentences;
    }

    public static GetSentenceResponseModel getAllSentencesFromDB()
    {
        try
        {
            String query = "SELECT * FROM cs122b_db143.valid_strings";

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Success. ");

            ArrayList<Sentence> sentences = new ArrayList<>();
            sentences = addToArray(sentences, rs);


            return GetSentenceResponseModel.buildModelFromList(sentences);
        }

        catch(SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query Failed: Unable to retrieve sentences.");
            e.printStackTrace();
        }

        ServiceLogger.LOGGER.info("No sentences were retrieved.");
        return GetSentenceResponseModel.buildModelFromList(null);

    }


    public static GetSentenceResponseModel retrieveSentencesFromDB(String sentence, int len)
    {
        //GetSentenceResponseModel responseModel;

        try
        {
            String query = "SELECT id, sentence, length FROM cs122b_db143.valid_strings WHERE sentence LIKE ? AND len LIKE ?;";

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ps.setString(1, sentence);
            ps.setInt(2, len);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Success.");

            ArrayList<Sentence> sentences = new ArrayList<>();
            sentences = addToArray(sentences, rs);

            return GetSentenceResponseModel.buildModelFromList(sentences);
        }

        catch(SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query Failed: Unable to retrieve sentences.");
            e.printStackTrace();
        }

        ServiceLogger.LOGGER.info("No sentences were retrieved.");
        return GetSentenceResponseModel.buildModelFromList(null);
    }

    public static boolean insertSentenceIntoDB(InsertSentenceRequestModel requestModel)
    {
        ServiceLogger.LOGGER.info("Inserting sentence into database...");
        try
        {
            String query = "INSERT INTO cs122b_db143.valid_strings (sentence, length) VALUES (?, ?)";

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ps.setString(1, requestModel.getSentence());
            ps.setInt(2, requestModel.getLen());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            return true;
        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Unable to insert sentence: " + requestModel.getSentence());
            e.printStackTrace();
        }
        return false;
    }

    public static GetSentenceResponseModel getSentenceFromID(int id)
    {
        ServiceLogger.LOGGER.info("Finding sentence from given id...");
        try
        {
            String query = "SELECT * FROM cs122b_db143.valid_strings WHERE id = ?;";

            PreparedStatement ps = BasicService.getCon().prepareStatement(query);
            ps.setInt(1, id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            ArrayList<Sentence> sentences = new ArrayList<>();
            sentences = addToArray(sentences, rs);

            return GetSentenceResponseModel.buildModelFromList(sentences);

        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.warning("Query Failed: Unable to retrieve sentence at id" + id);
            e.printStackTrace();
        }

        ServiceLogger.LOGGER.info("No sentence retrieved at id#" + id);
        return GetSentenceResponseModel.buildModelFromList(null);
    }
}
