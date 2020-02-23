package edu.uci.ics.sctse.service.movies.resources;

import edu.uci.ics.sctse.service.movies.database.MovieDatabase;
import edu.uci.ics.sctse.service.movies.database.MovieRecords;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.movies.models.SearchResponseModel;
import edu.uci.ics.sctse.service.movies.models.VerifyPrivilegeResponseModel;
import edu.uci.ics.sctse.service.movies.models.object_models.MovieModel;
import edu.uci.ics.sctse.service.movies.utilities.HTTPStatusCodes;
import edu.uci.ics.sctse.service.movies.utilities.ModelValidator;
import edu.uci.ics.sctse.service.movies.utilities.Validate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RED;
import static edu.uci.ics.sctse.service.movies.MovieService.ANSI_RESET;
import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.*;
import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.NO_MOVIES_FOUND;

@Path("get")
public class SearchMovieByID
{
    int SEARCH_BY_ID_PLEVEL = 4;

    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovieByID(@Context HttpHeaders headers, @PathParam("movieid") String movieid)
    {
        ServiceLogger.LOGGER.info("Retrieving movie from ID");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("EMAIL: " + email);
        ServiceLogger.LOGGER.info("SESSION ID: " + sessionID);

        try
        {
            SearchResponseModel responseModel;
            boolean hasPrivilege = Validate.hasPrivilege(email, SEARCH_BY_ID_PLEVEL);
            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);

            if (!hasPrivilege && MovieDatabase.isHidden(movieid))
            {
                return Validate.returnNoPrivilegeResponse();
            }
            else
            {
//                MovieModel[] movieModel = MovieRecords.searchByMovieId(movieid);
                MovieModel movieModel = MovieRecords.searchByMovieId(movieid);

                int resultCode = 0;
//                if (movieModel.length > 0)
                if (movieModel != null)
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
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

    }
}
