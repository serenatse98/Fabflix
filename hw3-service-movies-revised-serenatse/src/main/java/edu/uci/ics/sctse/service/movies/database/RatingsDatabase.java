package edu.uci.ics.sctse.service.movies.database;

import edu.uci.ics.sctse.service.movies.MovieService;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RED;
import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RESET;

public class RatingsDatabase
{
    public static void insertIntoRatingsDB(String id)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Inserting new movie into ratings table");

        try
        {
            String query = "INSERT INTO ratings (movieId, rating, numVotes) " +
                    "VALUES (?, 0.0, 0) ";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);
            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in RatingsDatabase.insertIntoRatingsDB";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static boolean updateRating(String movieid, float rating)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Updating rating for movie...");

        try
        {
            String getCols = "SELECT rating, numVotes FROM ratings WHERE movieId = ?;";
            PreparedStatement getPS = MovieService.getCon().prepareStatement(getCols);
            getPS.setString(1, movieid);

            ServiceLogger.LOGGER.info("Trying query: " + getPS.toString());
            ResultSet colRS = getPS.executeQuery();
            colRS.next();
            ServiceLogger.LOGGER.info("Query succeeded");


            String query = "UPDATE ratings SET rating = ?, numVotes = ? WHERE movieId = ? ";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            int newNumVotes = colRS.getInt("numVotes") + 1;
            float newRating = (colRS.getFloat("rating") * colRS.getInt("numVotes") + rating)/newNumVotes;

            ps.setFloat(1, newRating);
            ps.setInt(2, newNumVotes);
            ps.setString(3, movieid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");

            return true;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in RatingsDatabase.updateRating";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }
}
