package edu.uci.ics.sctse.service.movies.database;

import edu.uci.ics.sctse.service.movies.MovieService;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RED;
import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RESET;

public class StarsInMoviesDatabase
{
    public static boolean isStarInMovie(String starid, String movieid)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Checking if star is in movie...");

        try
        {
            String query = "SELECT * FROM stars_in_movies WHERE starid = ? AND movieid = ?;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, starid);
            ps.setString(2, movieid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Success. ");

            return rs.next();
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in StarsInMoviesDatabase.isStarInMovie";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static boolean addStarInDB(String starid, String movieid)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Adding star into stars_in_movies...");

        try
        {
            String query = "INSERT INTO stars_in_movies (starid, movieid) VALUES (?, ?);";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, starid);
            ps.setString(2, movieid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query Success. ");

            return true;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in StarsInMoviesDatabase.addStarInDB";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }
}
