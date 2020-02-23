package edu.uci.ics.sctse.service.movies.resources;

import edu.uci.ics.sctse.service.movies.database.MovieDatabase;
import edu.uci.ics.sctse.service.movies.database.StarDatabase;
import edu.uci.ics.sctse.service.movies.database.StarsInMoviesDatabase;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.*;
import edu.uci.ics.sctse.service.movies.models.object_models.StarModel;
import edu.uci.ics.sctse.service.movies.utilities.HTTPStatusCodes;
import edu.uci.ics.sctse.service.movies.utilities.ModelValidator;
import edu.uci.ics.sctse.service.movies.utilities.Validate;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.*;

@Path("star")
public class StarEndpoints
{
    private int ADD_STAR_PLEVEL = 3;
    private int ADD_STARSIN_PLEVEL = 3;

    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchStars(
            @Context HttpHeaders headers,
            @QueryParam("name") String name,
            @QueryParam("birthYear") int birthYear,
            @QueryParam("movieTitle") String movieTitle,
            @QueryParam("limit") int limit,
            @QueryParam("offset") int offset,
            @QueryParam("orderby") String orderby,
            @QueryParam("direction") String direction)
    {
        ServiceLogger.LOGGER.info("Searching Stars");

        String email = headers.getHeaderString("email");

        orderby = orderby == null ? "name" : "birthYear";
        direction = direction == null ? "asc" : "desc";

        limit = (limit != 10 && limit != 25 && limit != 50 && limit != 100) ? 10 : limit;
        offset = (offset < 0 || offset % limit != 0) ? 0 : offset;

        String n = (name == null) ? "" : "\t\"name\":\"" + name + "\",\n";
        String by = (birthYear == 0) ? "" : "\t\"birthYear\":" + birthYear + ",\n";
        String mv = (movieTitle == null) ? "" : "\t\"movieTitle\":\"" + movieTitle + "\",\n";

        String off = "\t\"offset\":" + offset + ",\n";
        String lim = "\t\"limit\":" + limit + ",\n";
        String dir = "\t\"direction\":\"" + direction + "\",\n";
        String order = "\t\"orderby\":\"" + orderby + "\"\n";
        String jsonText = "{\n" + n + by + mv + off + lim + dir + order + "}";

        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        try
        {
            StarSearchRequestModel requestModel;
            requestModel = (StarSearchRequestModel) ModelValidator.verifyModel(jsonText, StarSearchRequestModel.class);

            StarResponseModel responseModel;
            int resultCode;

            StarModel[] stars = StarDatabase.getStarList(requestModel);
            if (stars.length > 0)
            {
                resultCode = STARS_FOUND;
                responseModel = new StarResponseModel(resultCode, stars);
            }
            else
            {
                resultCode = NO_STARS_FOUND;
                responseModel = new StarResponseModel(resultCode, null);
            }

            return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchStarByID(@Context HttpHeaders headers, @PathParam("id") String id)
    {
        ServiceLogger.LOGGER.info("Searching Stars by id");

        try
        {
            StarIDResponseModel responseModel;
            int resultCode;

            StarModel stars = StarDatabase.getStarListFromID(id);
            if (stars != null)
            {
                resultCode = STARS_FOUND;
                responseModel = new StarIDResponseModel(resultCode, stars);
            }
            else
            {
                resultCode = NO_STARS_FOUND;
                responseModel = new StarIDResponseModel(resultCode, null);
            }

            return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }
    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewStar(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Adding new star");
        String email = headers.getHeaderString("email");

        try
        {
            AddStarRequestModel requestModel;
            requestModel = (AddStarRequestModel) ModelValidator.verifyModel(jsonText, AddStarRequestModel.class);

            boolean hasPrivilege = Validate.hasPrivilege(email, ADD_STAR_PLEVEL);
            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);

            if (!hasPrivilege)
            {
                return Validate.returnNoPrivilegeResponse();
            }
            else
            {
                GeneralResponseModel responseModel;
                int resultCode;

                if (StarDatabase.isStarInDB(requestModel.getName()))
                {
                    resultCode = STAR_ALREADY_EXISTS;
                }
                else
                {
                    String newID = StarDatabase.getNewStarID("ss0000001");
                    boolean addSuccess = requestModel.getBirthYear() > 0 ?
                            StarDatabase.addStar(newID, requestModel.getName(), requestModel.getBirthYear()) :
                            StarDatabase.addStar(newID, requestModel.getName());
                    if (addSuccess)
                    {
                        resultCode = STAR_ADDED;
                    }
                    else
                    {
                        resultCode = CANNOT_ADD_STAR;
                    }
                }

                responseModel = new GeneralResponseModel(resultCode);
                return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
            }
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }
    }

    @Path("starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarsIn(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Adding stars to stars in");
        String email = headers.getHeaderString("email");

        try
        {
            AddStarsInRequestModel requestModel;
            requestModel = (AddStarsInRequestModel) ModelValidator.verifyModel(jsonText, AddStarsInRequestModel.class);

            boolean hasPrivilege = Validate.hasPrivilege(email, ADD_STARSIN_PLEVEL);
            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);

            if (!hasPrivilege)
            {
                return Validate.returnNoPrivilegeResponse();
            }
            else
            {
                GeneralResponseModel responseModel;
                int resultCode;

                if (!MovieDatabase.isMovieInDB(requestModel.getMovieid()))
                {
                    resultCode = NO_MOVIES_FOUND;
                }
                else if (!StarDatabase.isStarInDB(StarDatabase.getStarName(requestModel.getStarid())))
                {
                    resultCode = CANNOT_ADD_STARIN;
                }
                else if (StarsInMoviesDatabase.isStarInMovie(requestModel.getStarid(), requestModel.getMovieid()))
                {
                    resultCode = STARIN_ALREADY_EXISTS;
                }
                else
                {
                    boolean addSuccess = StarsInMoviesDatabase.addStarInDB(requestModel.getStarid(), requestModel.getMovieid());
                    if (addSuccess)
                    {
                        resultCode = STARIN_ADDED;
                    }
                    else
                    {
                        resultCode = CANNOT_ADD_STARIN;
                    }
                }
                responseModel = new GeneralResponseModel(resultCode);
                return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
            }

        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }
    }
}
