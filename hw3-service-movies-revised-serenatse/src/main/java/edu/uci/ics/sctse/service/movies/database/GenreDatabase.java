package edu.uci.ics.sctse.service.movies.database;

import edu.uci.ics.sctse.service.movies.MovieService;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.AddMovieRequestModel;
import edu.uci.ics.sctse.service.movies.models.object_models.GenreModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RED;
import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RESET;

public class GenreDatabase
{
    public static GenreModel[] getMovieGenres(String title)
            throws ModelValidationException
    {
        try
        {
            String query = "SELECT G.id, G.name " +
                    "FROM movies AS M, genres_in_movies AS GM, genres AS G " +
                    "WHERE M.title = ? AND " +
                    "M.id = GM.movieId AND " +
                    "G.id = GM.genreId";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, title);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            ArrayList<GenreModel> genreArrayList = makeGenreModel(rs);

            GenreModel[] genreList = genreArrayList.toArray(new GenreModel[genreArrayList.size()]);
            ServiceLogger.LOGGER.info("GenreModel[]: " + Arrays.toString(genreList));

            return genreList;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in GenreDatabase.getMovieGenres";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static GenreModel[] getAllGenres()
            throws ModelValidationException
    {
        try
        {
            String query = "SELECT id, name FROM genres AS G;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            ArrayList<GenreModel> genreArrayList = makeGenreModel(rs);
            GenreModel[] genreList = genreArrayList.toArray(new GenreModel[genreArrayList.size()]);

            return genreList;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in GenreDatabase.getAllGenres";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    private static ArrayList<GenreModel> makeGenreModel(ResultSet rs)
            throws SQLException
    {
        ArrayList<GenreModel> genreArrayList = new ArrayList<>();
        while (rs.next())
        {
            GenreModel genre = new GenreModel(
                    rs.getInt("G.id"),
                    rs.getString("G.name"));
            genreArrayList.add(genre);
        }

        return genreArrayList;
    }

    public static int[] getGenreIDs(AddMovieRequestModel requestModel)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Getting genre IDs...");
        int[] genreList = new int[requestModel.getGenres().length];

        GenreModel[] genres = requestModel.getGenres();
        for (int i = 0; i < genres.length; ++i)
        {
            String name = genres[i].getName();
            int id = getGenreID(name);

            genreList[i] = id;
            if (!isGenreInDB(name))
            {
                insertNewGenreIntoDB(name);
            }
        }
        ServiceLogger.LOGGER.info("GenreID list: " + genreList);

        return genreList;
    }

    public static boolean isGenreInDB(String name)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Checking if genre is in DB...");

        try
        {
            String query = "SELECT * FROM genres WHERE name = ?";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, name);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Success. ");

            return rs.next();
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in GenreDatabase.isGenreInDB";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static void insertNewGenreIntoDB(String name)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Adding new genre in DB");

        try
        {
            String query = "INSERT INTO genres (name) VALUES (?)";
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, name);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");

        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in GenreDatabase.insertNewGenreIntoDB";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static void insertIntoGenresInMoviesDB(String movieid, int[] genres)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Inserting new movie into ratings table");

        try
        {
            for (int i = 0; i < genres.length; ++i)
            {
                String query = "INSERT INTO genres_in_movies (genreId, movieId) " +
                        "VALUES (?, ?) ";

                PreparedStatement ps = MovieService.getCon().prepareStatement(query);
                ps.setInt(1, genres[i]);
                ps.setString(2, movieid);
                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                ps.execute();
                ServiceLogger.LOGGER.info("Query succeeded");
            }
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in GenreDatabase.insertIntoGenresInMoviesDB";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static int getGenreID(String name)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Getting genreID from DB...");

        try
        {
            String query = "SELECT id FROM genres WHERE name = ?;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, name);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            if (rs.next())
                return rs.getInt("id");
            else
                return -1;

        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in GenreDatabase.getGenreID";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }
}
