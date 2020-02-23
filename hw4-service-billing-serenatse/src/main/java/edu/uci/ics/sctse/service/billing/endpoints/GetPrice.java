package edu.uci.ics.sctse.service.billing.endpoints;

import edu.uci.ics.sctse.service.billing.database.SalesDatabase;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.billing.models.GetPriceResponseModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("price")
public class GetPrice
{
    @Path("{movieId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrice(@PathParam("movieId") String movieId)
    {
        try
        {
            GetPriceResponseModel responseModel;
            int resultCode;

            float[] prices = SalesDatabase.getMoviePrice(movieId);
            responseModel = new GetPriceResponseModel(
                    3555, "Retrieved movie pricing", movieId, prices[0], prices[1]);
            return Response.status(Response.Status.OK).entity(responseModel).build();
        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.info("SQL Exception: " + e.getErrorCode());
            e.printStackTrace();
            GeneralResponseModel responseModel = new GeneralResponseModel(-1, "Internal server error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
    }
}
