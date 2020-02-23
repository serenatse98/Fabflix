package edu.uci.ics.sctse.service.movies.database;

import edu.uci.ics.sctse.service.movies.MovieService;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.SearchRequestModel;
import edu.uci.ics.sctse.service.movies.models.object_models.MovieModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RED;
import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RESET;

public class MovieRecords
{
    public static MovieModel[] searchMovies(SearchRequestModel requestModel, boolean hasPrivilege)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Searching movies...");
        MovieModel[] movieModelArray;

        try
        {
            String query = "SELECT DISTINCT M.id, M.title, M.director, M.year, M.hidden, R.rating, R.numVotes " +
                    "FROM movies AS M, ratings AS R, genres_in_movies AS G " +
                    "WHERE (M.title LIKE ? ) AND " +
                    "(M.director LIKE ? ) AND " +
                    "M.id = R.movieId AND " +
                    "G.movieid = M.id ";

            if (requestModel.getYear() > 0)
                query += "AND (M.year = " + requestModel.getYear() + " ) ";

            if (requestModel.getGenre() != null)
            {
                query += "AND G.genreId = " + GenreDatabase.getGenreID(requestModel.getGenre()) + " ";
            }


            query += "ORDER BY " + requestModel.getOrderby() + " " +
                    requestModel.getDirection() + ", title ASC " +
                    "LIMIT " + requestModel.getLimit() + " " +
                    "OFFSET " + requestModel.getOffset() + ";";

            ServiceLogger.LOGGER.info("Preparing statement...");
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            String pstitle = (requestModel.getTitle() == null) ? "%%" : "%" + requestModel.getTitle() + "%";
            ServiceLogger.LOGGER.info("PSTitle: " + pstitle);
            ps.setString(1, pstitle);

            String dir = (requestModel.getDirector() == null) ? "%%" : "%" + requestModel.getDirector() + "%";
            ServiceLogger.LOGGER.info("DIRECTOR: " + dir);
            ps.setString(2, dir);

            //        Boolean hidden = (requestModel.isHidden()) ?

            ServiceLogger.LOGGER.info("Statement prepared.");

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            ArrayList<MovieModel> movieModelList;

            if (!hasPrivilege)
            {
                ServiceLogger.LOGGER.info("Making model for insufficient priv");
                movieModelList = makeModel(rs);
            }
            else
            {
                ServiceLogger.LOGGER.info("Making model for sufficient level");
                movieModelList = makePrivilegeModel(rs);
            }

            movieModelArray = movieModelList.toArray(new MovieModel[movieModelList.size()]);
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieRecords.searchMovies";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
        return movieModelArray;
    }

    public static MovieModel searchByMovieId(String movieid)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Searching movie by id");
        MovieModel movieModel;

        try
        {
            String query = "SELECT DISTINCT M.id, M.title, M.director, M.year, " +
                    "M.backdrop_path,  M.budget, M.overview, M.poster_path, " +
                    "M.revenue, M.hidden, R.rating, R.numVotes " +
                    "FROM movies AS M, ratings AS R " +
                    "WHERE M.id = ? AND " +
                    "M.id = R.movieId;" ;

            ServiceLogger.LOGGER.info("Preparing statement...");
            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, movieid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

//            ArrayList<MovieModel> movieModelList = makeModelForIDSearch(rs);
//            movieModel = movieModelList.toArray(new MovieModel[movieModelList.size()]);
//            return movieModel;
            return makeModelForIDSearch(rs);

        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieRecords.searchMoviesByID";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    private static ArrayList<MovieModel> makeModel(ResultSet rs)
            throws SQLException, ModelValidationException
    {
        ServiceLogger.LOGGER.info("Making Movie Model...");
        ArrayList<MovieModel> movieModelList = new ArrayList<>();
        while (rs.next())
        {
            String title = rs.getString("M.title");
            String id = rs.getString("M.id");
            ServiceLogger.LOGGER.info("GOT title: " + title);

            if (MovieDatabase.isHidden(id))
            {
                rs.next();
            }
            else
            {
                MovieModel movieModel = new MovieModel(
                        id,
                        title,
                        rs.getString("M.director"),
                        rs.getInt("M.year"),
                        rs.getFloat("R.rating"),
                        rs.getInt("R.numVotes"));
                ServiceLogger.LOGGER.info("MOVIES: " + movieModel);
                movieModelList.add(movieModel);
            }

        }

        return movieModelList;
    }

    private static ArrayList<MovieModel> makePrivilegeModel(ResultSet rs)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Making Movie Model...");
        ArrayList<MovieModel> movieModelList = new ArrayList<>();
        while (rs.next())
        {
            String title = rs.getString("M.title");
            ServiceLogger.LOGGER.info("GOT title: " + title);

            MovieModel movieModel = new MovieModel(
                    rs.getString("M.id"),
                    title,
                    rs.getString("M.director"),
                    rs.getInt("M.year"),
                    rs.getFloat("R.rating"),
                    rs.getInt("R.numVotes"),
                    rs.getBoolean("M.hidden"));
            ServiceLogger.LOGGER.info("MOVIES: " + movieModel);
            movieModelList.add(movieModel);

        }

        return movieModelList;
    }

    private static MovieModel makeModelForIDSearch(ResultSet rs)
            throws ModelValidationException, SQLException
    {
        ServiceLogger.LOGGER.info("Making Movie Model for Search by ID...");
//        ArrayList<MovieModel> movieModelList = new ArrayList<>();
        MovieModel movieModel = null;
        if (rs.next())
        {
            String title = rs.getString("M.title");
            ServiceLogger.LOGGER.info("GOT title: " + title);

            movieModel = new MovieModel(
                    rs.getString("M.id"),
                    title,
                    rs.getString("M.director"),
                    rs.getInt("M.year"),
                    rs.getString("M.backdrop_path"),
                    rs.getObject("M.budget"),
                    rs.getString("M.overview"),
                    rs.getString("M.poster_path"),
                    rs.getObject("M.revenue"),
                    rs.getFloat("R.rating"),
                    rs.getInt("R.numVotes"),
                    GenreDatabase.getMovieGenres(title),
                    StarDatabase.getStarList(title));
            ServiceLogger.LOGGER.info("MOVIES: " + movieModel);
//            movieModelList.add(movieModel);

        }

        return movieModel;

    }

    public static MovieModel[] searchByFirstLetter(
            String letter, int offset, int limit, String orderby, String direction, boolean hasPrivilege)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("searching by first letter...");
        MovieModel[] movieModelArray;

        try
        {
            String query = "SELECT DISTINCT M.id, M.title, M.director, M.year, M.hidden, R.rating, R.numVotes " +
                    "FROM movies AS M, ratings AS R, genres_in_movies AS G " +
                    "WHERE M.title LIKE ? AND M.id = R.movieId AND G.movieid = M.id ";

            query += "ORDER BY " + orderby + " " +
                    direction + ", title ASC " +
                    "LIMIT " + limit + " " +
                    "OFFSET " + offset + ";";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, letter+"%");

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            ArrayList<MovieModel> movieModelList;

            if (!hasPrivilege)
            {
                ServiceLogger.LOGGER.info("Making model for insufficient priv");
                movieModelList = makeModel(rs);
            }
            else
            {
                ServiceLogger.LOGGER.info("Making model for sufficient level");
                movieModelList = makePrivilegeModel(rs);
            }

            movieModelArray = movieModelList.toArray(new MovieModel[movieModelList.size()]);

            return movieModelArray;


        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in MovieRecords.searchByFirstLetter";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }
}
