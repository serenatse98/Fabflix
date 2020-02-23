package edu.uci.ics.sctse.service.movies.resources;

import edu.uci.ics.sctse.service.movies.MovieService;
import edu.uci.ics.sctse.service.movies.database.MovieDatabase;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.movies.utilities.HTTPStatusCodes;
import edu.uci.ics.sctse.service.movies.utilities.ModelValidator;
import edu.uci.ics.sctse.service.movies.utilities.Validate;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.*;

@Path("delete")
public class DeleteMovie
{
    private int DELETE_PLEVEL = 3;

    @Path("{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovie(@Context HttpHeaders headers, @PathParam("movieid") String movieid)
    {
        ServiceLogger.LOGGER.info("Deleting movie from ID");

        String email = headers.getHeaderString("email");

        try
        {
            GeneralResponseModel responseModel;
            int resultCode;
            boolean hasPrivilege = Validate.hasPrivilege(email, DELETE_PLEVEL);
            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);

            if (!hasPrivilege)
            {
                return Validate.returnNoPrivilegeResponse();
            }
            else if (MovieDatabase.isHidden(movieid))
            {
                resultCode = MOVIE_ALREADY_REMOVED;
            }
            else if (!MovieDatabase.isMovieInDB(movieid))
            {
                resultCode = CANNOT_REMOVE_MOVIE;
            }
            else
            {
                boolean movieIsRemoved = MovieDatabase.removeMovie(movieid);

                if (movieIsRemoved)
                {
                    resultCode = MOVIE_REMOVED;
                }
                else
                {
                    resultCode = CANNOT_REMOVE_MOVIE;
                }
            }

            responseModel = new GeneralResponseModel(resultCode);
            return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }


    }
}
