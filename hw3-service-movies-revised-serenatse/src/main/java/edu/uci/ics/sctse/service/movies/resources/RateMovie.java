package edu.uci.ics.sctse.service.movies.resources;

import edu.uci.ics.sctse.service.movies.database.MovieDatabase;
import edu.uci.ics.sctse.service.movies.database.RatingsDatabase;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.movies.models.RatingRequestModel;
import edu.uci.ics.sctse.service.movies.utilities.HTTPStatusCodes;
import edu.uci.ics.sctse.service.movies.utilities.ModelValidator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.*;

@Path("rating")
public class RateMovie
{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response rateMovie(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to rate movie");

        try
        {
            RatingRequestModel requestModel;
            requestModel = (RatingRequestModel) ModelValidator.verifyModel(jsonText, RatingRequestModel.class);

            GeneralResponseModel responseModel;
            int resultCode;

            if (!MovieDatabase.isMovieInDB(requestModel.getId()))
            {
                resultCode = NO_MOVIES_FOUND;
            }
            else if (requestModel.getRating() < 0.0 || requestModel.getRating() > 10.0)
            {
                resultCode = CANNOT_UPDATE_RATING;
            }
            else
            {
                boolean updateSuccess = RatingsDatabase.updateRating(requestModel.getId(), requestModel.getRating());
                if (updateSuccess)
                {
                    resultCode = RATING_UPDATED;
                }
                else
                {
                    resultCode = CANNOT_UPDATE_RATING;
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
