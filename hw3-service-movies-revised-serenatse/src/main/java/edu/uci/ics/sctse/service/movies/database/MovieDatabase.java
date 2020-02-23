package edu.uci.ics.sctse.service.movies.database;

import edu.uci.ics.sctse.service.movies.MovieService;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.AddMovieRequestModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RED;
import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RESET;

public class MovieDatabase
{
    public static boolean isHidden(String movieid)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Getting hidden status of movie...");

        try
        {
            String query = "SELECT hidden FROM movies WHERE id = ?;";

            ServiceLogger.LOGGER.info("Preparing statement...");
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            if (rs.next())
                return rs.getInt("hidden") == 1;

            else
                return false;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieDatabase.isHidden";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static boolean isMovieInDB(String title, String director, int year)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Checking if movie is in Database...");
        try
        {
            String query = "SELECT * FROM movies WHERE title = ? AND director = ? AND year = ?; ";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, title);
            ps.setString(2, director);
            ps.setInt(3, year);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Success. ");

            return rs.next();
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieDatabase.isMovieInDB";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static boolean isMovieInDB(String movieid)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Checking if movie is in Database...");
        try
        {
            String query = "SELECT * FROM movies WHERE id = ?; ";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Success. ");

            return rs.next();
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieDatabase.isMovieInDB";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static boolean addMovie(AddMovieRequestModel requestModel, String newID)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Adding movie to DB...");
        try
        {
            String query = "INSERT INTO movies (id, title, year, director, backdrop_path, budget, overview, poster_path, revenue) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

            ServiceLogger.LOGGER.info("Preparing statement...");
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, newID);
            ps.setString(2, requestModel.getTitle());
            ps.setInt(3, requestModel.getYear());
            ps.setString(4, requestModel.getDirector());

            if (requestModel.getBackdrop_path() == null)
                ps.setNull(5, Types.VARCHAR);
            else
                ps.setString(5, requestModel.getBackdrop_path());

            if (requestModel.getBudget() > 0)
                ps.setInt(6, requestModel.getBudget());
            else
                ps.setNull(6, Types.INTEGER);

            if (requestModel.getOverview() == null)
                ps.setNull(7, Types.VARCHAR);
            else
                ps.setString(7, requestModel.getOverview());

            if (requestModel.getPoster_path() == null)
                ps.setNull(8, Types.VARCHAR);
            else
                ps.setString(8, requestModel.getPoster_path());

            if (requestModel.getRevenue() > 0)
                ps.setInt(9, requestModel.getBudget());
            else
                ps.setNull(9, Types.INTEGER);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");

            return true;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieDatabase.addMovie";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static String getNewMovieID(String newID)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Getting new movie id");

        try
        {
            String checkID = "SELECT * FROM movies WHERE id = 'cs0000001' ";
            PreparedStatement check = MovieService.getCon().prepareStatement(checkID);
            ServiceLogger.LOGGER.info("Trying query: " + check.toString());
            ResultSet result = check.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            if (result.next())
            {
                ServiceLogger.LOGGER.info("getting new id");
                newID = "cs";
                String maxID;
                int idNum;

                String query = "SELECT MAX(id) AS maxID FROM movies WHERE id LIKE 'cs%'";

                PreparedStatement ps = MovieService.getCon().prepareStatement(query);

                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                ResultSet rs = ps.executeQuery();
                ServiceLogger.LOGGER.info("Query succeeded");

                rs.first();
                maxID = rs.getString("maxID");
                ServiceLogger.LOGGER.info("got max id: " + maxID);

                idNum = Integer.parseInt(maxID.substring(2));
                idNum += 1;
                ServiceLogger.LOGGER.info("new id num: " + idNum);
                newID = newID + String.format("%07d", idNum);

                ServiceLogger.LOGGER.info("NEW ID: " + newID);
            }
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieDatabase.getNewMovieID";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }

        return newID;
    }

    public static boolean removeMovie(String movieid)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Removing movie from DB...");

        try
        {
            String query = "UPDATE movies SET hidden = 1 WHERE id = ?;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");

            return true;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieDatabase.removeMovie";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static String getTitleFromID(String id)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Getting movie title...");

        try
        {
            String query = "SELECT title FROM movies WHERE id = ?;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            if (rs.next())
                return rs.getString("title");
            else
                return null;

        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieDatabase.getTitleFromID";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

}
