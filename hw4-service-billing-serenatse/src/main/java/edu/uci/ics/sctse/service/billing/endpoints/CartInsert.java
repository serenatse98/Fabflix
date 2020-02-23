package edu.uci.ics.sctse.service.billing.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.billing.core.ResultCodes;
import edu.uci.ics.sctse.service.billing.core.Validate;
import edu.uci.ics.sctse.service.billing.database.CartDatabase;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.models.CartInsertRequestModel;
import edu.uci.ics.sctse.service.billing.models.GeneralResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("cart")
public class CartInsert
{
    private int DUPLICATE_DATA_ERROR = 1062;

    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertIntoCart(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to insert into cart");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

//        String email = headers.getHeaderString("email");
//        String sessionID = headers.getHeaderString("sessionID");
//        ServiceLogger.LOGGER.info("EMAIL: " + email);
//        ServiceLogger.LOGGER.info("SESSION ID: " + sessionID);

        ObjectMapper mapper = new ObjectMapper();
        CartInsertRequestModel requestModel;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, CartInsertRequestModel.class);
            ServiceLogger.LOGGER.info("Mapped to JSON");

            GeneralResponseModel responseModel;
            int resultCode = 0;

            if (!Validate.hasValidEmail(requestModel.getEmail()))
            {
                ServiceLogger.LOGGER.info("Invalid email format");
                resultCode = -11;
            }
            else if (requestModel.getEmail().length() > 50 ||
                    requestModel.getEmail().length() <= 0)
            {
                ServiceLogger.LOGGER.info("Invalid email length");
                resultCode = -10;
            }
            else if (requestModel.getQuantity() <= 0)
            {
                ServiceLogger.LOGGER.info("Quantity must be positive number");
                resultCode = 33;
            }
            else
            {
                // try inserting catch SQL error if has duplicate
                try
                {
                    CartDatabase.insertIntoCartDB(
                            requestModel.getEmail(), requestModel.getMovieId(), requestModel.getQuantity());
                    ServiceLogger.LOGGER.info("Shopping endpoints item inserted successfully.");
                    resultCode = 3100;
                }
                catch (SQLException e)
                {
                    if (e.getErrorCode() == DUPLICATE_DATA_ERROR)
                    {
                        ServiceLogger.LOGGER.info("Duplicate entry in endpoints database");
                        resultCode = 311;
                    }
//                    if (e instanceof SQLIntegrityConstraintViolationException)
//                    {
//                        ServiceLogger.LOGGER.info("Duplicate entry in endpoints database");
//                        resultCode = 311;
//                    }
                }
            }
            String message = ResultCodes.resultMessage(resultCode);
            responseModel = new GeneralResponseModel(resultCode, message);

            String response = ResultCodes.getResponse(resultCode);
            switch (response)
            {
                case "200 OK":
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                case "400 Bad Request":
                    return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
                case "500 Internal Server Error":
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }

        }
        catch (IOException e)
        {
            if (e instanceof JsonMappingException)
            {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                ServiceLogger.LOGGER.info("JSON not in correct format.");
                e.printStackTrace();
                GeneralResponseModel responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException)
            {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                ServiceLogger.LOGGER.info("JSON not in correct format..");
                GeneralResponseModel responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else
            {
                ServiceLogger.LOGGER.warning("IOException. Internal Server Error");
                e.printStackTrace();
                GeneralResponseModel responseModel = new GeneralResponseModel(-1, "Internal server error.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        // SHOULD NOT REACH HERE
        ServiceLogger.LOGGER.warning("should NOT reach this...");
        GeneralResponseModel serverError = new GeneralResponseModel(-1, "Internal Server Error.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(serverError).build();
    }

}
