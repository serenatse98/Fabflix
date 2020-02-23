package edu.uci.ics.sctse.service.movies.resources;

import edu.uci.ics.sctse.service.movies.database.GenreDatabase;
import edu.uci.ics.sctse.service.movies.database.MovieDatabase;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.AddGenreRequestModel;
import edu.uci.ics.sctse.service.movies.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.movies.models.GenreResponseModel;
import edu.uci.ics.sctse.service.movies.models.object_models.GenreModel;
import edu.uci.ics.sctse.service.movies.utilities.HTTPStatusCodes;
import edu.uci.ics.sctse.service.movies.utilities.ModelValidator;
import edu.uci.ics.sctse.service.movies.utilities.Validate;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.*;

@Path("genre")
public class GenreEndpoints
{
    private int GENRE_GET_PLEVEL = 3;
    private int GENRE_ADD_PLEVEL = 3;
    private int GENRE_GET_BY_ID_PLEVEL = 3;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllGenres(@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Getting all genres");
        String email = headers.getHeaderString("email");

        try
        {
            GenreResponseModel responseModel;
            int resultCode;

//            boolean hasPrivilege = Validate.hasPrivilege(email, GENRE_GET_PLEVEL);
//            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);
//
//            if (!hasPrivilege)
//            {
//                return Validate.returnNoPrivilegeResponse();
//            }
//            else
//            {
            GenreModel[] genres = GenreDatabase.getAllGenres();
            resultCode = GENRES_RETRIEVED;
            responseModel = new GenreResponseModel(resultCode, genres);
//            }

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
    public Response addGenre(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Adding new genre");
        String email = headers.getHeaderString("email");

        try
        {
            AddGenreRequestModel requestModel;
            requestModel = (AddGenreRequestModel) ModelValidator.verifyModel(jsonText, AddGenreRequestModel.class);

            boolean hasPrivilege = Validate.hasPrivilege(email, GENRE_ADD_PLEVEL);
            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);

            if (!hasPrivilege)
            {
                return Validate.returnNoPrivilegeResponse();
            }
            else
            {
                GeneralResponseModel responseModel;
                int resultCode;

                if (GenreDatabase.isGenreInDB(requestModel.getName()))
                {
                    resultCode = CANNOT_ADD_GENRE;
                }
                else
                {
                    GenreDatabase.insertNewGenreIntoDB(requestModel.getName());
                    resultCode = GENRE_ADDED;
                }

                responseModel = new GeneralResponseModel(resultCode);
                return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
            }

        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }
    }

    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieGenresByID(@Context HttpHeaders headers, @PathParam("movieid") String movieid)
    {
        ServiceLogger.LOGGER.info("Getting movie's genres");
        String email = headers.getHeaderString("email");

        try
        {
            GeneralResponseModel responseModel;
            int resultCode;

            boolean hasPrivilege = Validate.hasPrivilege(email, GENRE_GET_BY_ID_PLEVEL);
            ServiceLogger.LOGGER.info("Does user have privilege: " + hasPrivilege);

            if (!hasPrivilege)
            {
                return Validate.returnNoPrivilegeResponse();
            }
            else
            {
                if (MovieDatabase.isMovieInDB(movieid))
                {
                    String title = MovieDatabase.getTitleFromID(movieid);
                    GenreModel[] genres = GenreDatabase.getMovieGenres(title);
                    resultCode = GENRES_RETRIEVED;
                    responseModel = new GenreResponseModel(resultCode, genres);
                }
                else
                {
                    resultCode = NO_MOVIES_FOUND;
                    responseModel = new GeneralResponseModel(resultCode);
                }
                return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
            }

        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }


    }
}
