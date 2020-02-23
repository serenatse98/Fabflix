package edu.uci.ics.sctse.service.movies.resources;


import edu.uci.ics.sctse.service.movies.database.MovieDatabase;
import edu.uci.ics.sctse.service.movies.database.MovieRecords;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.movies.models.SearchRequestModel;
import edu.uci.ics.sctse.service.movies.models.SearchResponseModel;
import edu.uci.ics.sctse.service.movies.models.object_models.MovieModel;
import edu.uci.ics.sctse.service.movies.utilities.HTTPStatusCodes;
import edu.uci.ics.sctse.service.movies.utilities.ModelValidator;
import edu.uci.ics.sctse.service.movies.utilities.Validate;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RED;
import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RESET;
import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.*;

@Path("search")
public class SearchMovie
{
    int SEARCH_MOVIES_PLEVEL = 3;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSearchRequest(
            @Context HttpHeaders headers,
            @QueryParam("title") String title,
            @QueryParam("genre") String genre,
            @QueryParam("year") int year,
            @QueryParam("director") String director,
            @QueryParam("hidden") boolean hidden,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit,
            @DefaultValue("rating") @QueryParam("orderby") String orderby,
            @DefaultValue("desc") @QueryParam("direction") String direction)
    {

        ServiceLogger.LOGGER.info("Received search request");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("EMAIL: " + email);
        ServiceLogger.LOGGER.info("SESSION ID: " + sessionID);

        limit = (limit != 10 && limit != 25 && limit != 50 && limit != 100) ? 10 : limit;
        offset = (offset < 0 || offset % limit != 0) ? 0 : offset;

        String t = (title == null) ? "" : "\t\"title\":\"" + title + "\",\n";
        String g = (genre == null) ? "" : "\t\"genre\":\"" + genre + "\",\n";
        String y = (year == 0) ? "" : "\t\"year\":" + year + ",\n";
        String d = (director == null) ? "" : "\t\"director\":\"" + director + "\",\n";

        String h = "";


        String off = "\t\"offset\":" + offset + ",\n";
        String lim = "\t\"limit\":" + limit + ",\n";
        String sort = "\t\"orderby\":\"" + orderby + "\",\n";
        String order = "\t\"direction\":\"" + direction + "\"\n";
        String jsonText = "{\n" + t + g + y + d + h + off + lim + sort + order + "}";
        ServiceLogger.LOGGER.info("jsonText: " + jsonText);

        SearchRequestModel requestModel;
        try
        {
            requestModel = (SearchRequestModel) ModelValidator.verifyModel(jsonText, SearchRequestModel.class);


            SearchResponseModel responseModel;
            boolean hasPrivilege = Validate.hasPrivilege(email, SEARCH_MOVIES_PLEVEL);
            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);

            MovieModel[] movieModel = MovieRecords.searchMovies(requestModel, hasPrivilege);

            int resultCode;
            if (movieModel.length > 0)
            {
                resultCode = MOVIES_FOUND;
                responseModel = new SearchResponseModel(resultCode, movieModel);
            }

            else
            {
                resultCode = NO_MOVIES_FOUND;
                GeneralResponseModel generalResponseModel = new GeneralResponseModel(resultCode);
                return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(generalResponseModel).build();

            }

            return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode))
                    .entity(responseModel)
                    .header("Access-Control-Expose-Headers", "*")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }
    }

    @Path("{letter}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByFirstLetter(
            @Context HttpHeaders headers,
            @PathParam("letter") String letter,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit,
            @DefaultValue("rating") @QueryParam("orderby") String orderby,
            @DefaultValue("desc") @QueryParam("direction") String direction)
    {
        ServiceLogger.LOGGER.info("Received search request for first letter");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("EMAIL: " + email);
        ServiceLogger.LOGGER.info("SESSION ID: " + sessionID);

        limit = (limit != 10 && limit != 25 && limit != 50 && limit != 100) ? 10 : limit;
        offset = (offset < 0 || offset % limit != 0) ? 0 : offset;

        try
        {
            SearchResponseModel responseModel;
            boolean hasPrivilege = Validate.hasPrivilege(email, SEARCH_MOVIES_PLEVEL);
            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);

            MovieModel[] movieModel = MovieRecords.searchByFirstLetter(letter, offset, limit, orderby, direction, hasPrivilege);

            int resultCode;
            if (movieModel.length > 0)
            {
                resultCode = MOVIES_FOUND;
                responseModel = new SearchResponseModel(resultCode, movieModel);
                return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
            }

            else
            {
                resultCode = NO_MOVIES_FOUND;
                GeneralResponseModel generalResponseModel = new GeneralResponseModel(resultCode);
                return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(generalResponseModel).build();
            }

        }
        catch (ModelValidationException e)
        {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }
    }
}

