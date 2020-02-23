package edu.uci.ics.sctse.service.movies.database;

import edu.uci.ics.sctse.service.movies.MovieService;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.StarSearchRequestModel;
import edu.uci.ics.sctse.service.movies.models.object_models.StarModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RED;
import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RESET;

public class StarDatabase
{
    public static StarModel[] getStarList(String title)
            throws SQLException
    {
        ServiceLogger.LOGGER.info("Getting starlist for movie search...");
        String query = "SELECT DISTINCT S.id, S.name, S.birthYear " +
                "FROM stars AS S, stars_in_movies AS SM, movies AS M " +
                "WHERE S.id = SM.starId AND " +
                "M.title = ? AND " +
                "M.id = SM.movieId;";
        PreparedStatement ps = MovieService.getCon().prepareStatement(query);
        ps.setString(1, title);

        ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
        ResultSet rs = ps.executeQuery();
        ServiceLogger.LOGGER.info("Query succeeded");

        ArrayList<StarModel> starArrayList = makeStarModel(rs);
        StarModel[] starList = starArrayList.toArray(new StarModel[starArrayList.size()]);

        return starList;
    }

    public static StarModel[] getStarList(StarSearchRequestModel requestModel)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("getting star list for star search...");

        try
        {
            String query = "SELECT DISTINCT S.id, S.name, S.birthYear " +
                    "FROM stars AS S, movies AS M " +
                    "WHERE S.name LIKE ? AND M.title LIKE ?  ";

            if (requestModel.getBirthYear() > 0)
                query += "AND S.birthYear = " + requestModel.getBirthYear() + " ";


            String secondOrder = requestModel.getOrderby().equals("name") ? "birthYear" : "name";

            query += "ORDER BY " + requestModel.getOrderby() + " " +
                    requestModel.getDirection() + ", " +
                    secondOrder + " ASC" + " LIMIT ? OFFSET ?;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            String name = requestModel.getName() == null ? "%%" : "%"+requestModel.getName()+"%";
            String title = requestModel.getMovieTitle() == null ? "%%" : "%"+requestModel.getMovieTitle()+"%";

            ps.setString(1, name);
            ps.setString(2, title);

            ps.setInt(3, requestModel.getLimit());
            ps.setInt(4, requestModel.getOffset());

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            ArrayList<StarModel> starArrayList = makeStarModel(rs);
            StarModel[] starList = starArrayList.toArray(new StarModel[starArrayList.size()]);

            return starList;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in StarDatabase.getStarList(requestModel)";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static StarModel getStarListFromID(String id)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Getting stars list from id...");

        try
        {
            String query = "SELECT id, name, birthYear FROM stars WHERE id = ?;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, id);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            if (rs.next())
            {
                StarModel starModel = new StarModel(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getObject("birthYear"));
                return starModel;
            }
            else
                return null;

        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in StarDatabase.getStarListFromID";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    private static ArrayList<StarModel> makeStarModel(ResultSet rs)
            throws SQLException
    {
        ArrayList<StarModel> starArrayList = new ArrayList<>();
        while (rs.next())
        {
            StarModel star = new StarModel(
                    rs.getString("S.id"),
                    rs.getString("S.name"),
                    rs.getObject("S.birthYear"));
            starArrayList.add(star);
        }

        return starArrayList;
    }

    public static boolean isStarInDB(String name)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Checking if star is in DB...");
        try
        {
            String query = "SELECT * FROM stars WHERE name = ?;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, name);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            return rs.next();
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in StarDatabase.isStarInDB";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static String getStarName(String starid)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Getting star name");

        try
        {
            String query = "SELECT name FROM stars WHERE id = ?;";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, starid);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            if (rs.next())
                return  rs.getString("name");
            else
                return null;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in StarDatabase.isStarInDB";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static boolean addStar(String newID, String name, int birthYear)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Adding star to DB...");

        try
        {
            String query = "INSERT INTO stars (id, name, birthYear) " +
                    "VALUES (?, ?, ?);";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, newID);
            ps.setString(2, name);
            ps.setInt(3, birthYear);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");

            return true;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in StarDatabase.addStar(3)";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static boolean addStar(String newID, String name)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Adding star to DB...");

        try
        {
            String query = "INSERT INTO stars (id, name) " +
                    "VALUES (?, ?);";

            PreparedStatement ps = MovieService.getCon().prepareStatement(query);
            ps.setString(1, newID);
            ps.setString(2, name);

            ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Query succeeded");

            return true;
        }
        catch (SQLException e)
        {
            String warning = "SQL Exception in StarDatabase.addStar(3)";
            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
            e.printStackTrace();
            throw new ModelValidationException(warning, e);
        }
    }

    public static String getNewStarID(String newID)
            throws ModelValidationException
    {
        ServiceLogger.LOGGER.info("Getting new star id");

        try
        {
            String checkID = "SELECT * FROM stars WHERE id = 'ss0000001' ";
            PreparedStatement check = MovieService.getCon().prepareStatement(checkID);
            ServiceLogger.LOGGER.info("Trying query: " + check.toString());
            ResultSet result = check.executeQuery();
            ServiceLogger.LOGGER.info("Query succeeded");

            if (result.next())
            {
                ServiceLogger.LOGGER.info("getting new id");
                newID = "ss";
                String maxID;
                int idNum;

                String query = "SELECT MAX(id) AS maxID FROM stars WHERE id LIKE 'ss%'";

                PreparedStatement ps = MovieService.getCon().prepareStatement(query);

                ServiceLogger.LOGGER.info("Trying query: " + ps.toString());
                ResultSet rs = ps.executeQuery();
                ServiceLogger.LOGGER.info("Query succeeded");

                rs.first();
                maxID = rs.getString("maxID");

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
}
