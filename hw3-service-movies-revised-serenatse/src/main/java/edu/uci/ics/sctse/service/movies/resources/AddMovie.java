package edu.uci.ics.sctse.service.movies.resources;

import edu.uci.ics.sctse.service.movies.database.GenreDatabase;
import edu.uci.ics.sctse.service.movies.database.MovieDatabase;
import edu.uci.ics.sctse.service.movies.database.RatingsDatabase;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.*;
import edu.uci.ics.sctse.service.movies.utilities.HTTPStatusCodes;
import edu.uci.ics.sctse.service.movies.utilities.ModelValidator;
import edu.uci.ics.sctse.service.movies.utilities.Validate;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.*;

@Path("add")
public class AddMovie
{
    private int ADD_MOVIE_PLEVEL = 3;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovie(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to add movie");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " + email);
        ServiceLogger.LOGGER.info("SESSION ID: " + sessionID);
        ServiceLogger.LOGGER.info("TRANS ID: " + transactionID);

        AddMovieRequestModel requestModel;
        try
        {
            requestModel = (AddMovieRequestModel) ModelValidator.verifyModel(jsonText, AddMovieRequestModel.class);

            boolean hasPrivilege = Validate.hasPrivilege(email, ADD_MOVIE_PLEVEL);
            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);

            if (!hasPrivilege)
            {
                return Validate.returnNoPrivilegeResponse();
            }
            else
            {
                GeneralResponseModel responseModel;
                int resultCode;

                if (MovieDatabase.isMovieInDB(requestModel.getTitle(), requestModel.getDirector(), requestModel.getYear()))
                {
                    resultCode = MOVIE_ALREADY_EXISTS;
                    responseModel = new GeneralResponseModel(resultCode);
                }
                else
                {
                    String newID = MovieDatabase.getNewMovieID("cs0000001");
                    boolean addSuccess = MovieDatabase.addMovie(requestModel, newID);

                    if (addSuccess)
                    {
                        resultCode = MOVIE_ADDED;
                        int[] genreid = GenreDatabase.getGenreIDs(requestModel);
                        GenreDatabase.insertIntoGenresInMoviesDB(newID, genreid);
                        RatingsDatabase.insertIntoRatingsDB(newID);
                        responseModel = new AddMovieResponseModel(resultCode, newID, genreid);
                    }
                    else
                    {
                        resultCode = CANNOT_ADD_MOVIE;
                        responseModel = new GeneralResponseModel(resultCode);
                    }
                }

                return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
            }
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }
    }
}
