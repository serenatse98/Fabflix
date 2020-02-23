package edu.uci.ics.sctse.service.billing.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.billing.core.ResultCodes;
import edu.uci.ics.sctse.service.billing.core.Validate;
import edu.uci.ics.sctse.service.billing.database.CartDatabase;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.models.CartRetrieveRequestModel;
import edu.uci.ics.sctse.service.billing.models.CartRetrieveResponseModel;
import edu.uci.ics.sctse.service.billing.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.billing.objects.Items;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("cart")
public class CartRetrieve
{
    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveFromCart(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to update cart");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

//        String email = headers.getHeaderString("email");
//        String sessionID = headers.getHeaderString("sessionID");
//        ServiceLogger.LOGGER.info("EMAIL: " + email);
//        ServiceLogger.LOGGER.info("SESSION ID: " + sessionID);

        ObjectMapper mapper = new ObjectMapper();
        CartRetrieveRequestModel requestModel;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, CartRetrieveRequestModel.class);
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
            String message = ResultCodes.resultMessage(resultCode);
            responseModel = new GeneralResponseModel(resultCode, message);

            if (resultCode == 0)
            {
                try
                {
                    if (!CartDatabase.isEmailInCartsDB(requestModel.getEmail()))
                    {
                        ServiceLogger.LOGGER.info("Email not found in carts");
                        resultCode = 312;
                        message = ResultCodes.resultMessage(resultCode);
                        responseModel = new GeneralResponseModel(resultCode, message);
                    }
                    else
                    {
                        Items[] items = CartDatabase.retrieveFromCartDB(requestModel.getEmail());
                        resultCode = 3130;
                        message = ResultCodes.resultMessage(resultCode);
                        responseModel = new CartRetrieveResponseModel(resultCode, message, items);
                    }
                }
                catch (SQLException e)
                {
                    ServiceLogger.LOGGER.info("SQL Exception: " + e.getErrorCode());
                    e.printStackTrace();
                    resultCode = -1;
                    message = ResultCodes.resultMessage(resultCode);
                    responseModel = new GeneralResponseModel(resultCode, message);
                }
            }

            String response = ResultCodes.getResponse(resultCode);
            switch (response)
            {
                case "200 OK":
                    return Response.status(Response.Status.OK)
                            .header("Access-Control-Expose-Headers", "*")
                            .header("Access-Control-Allow-Origin", "*")
                            .entity(responseModel).build();
                case "400 Bad Request":
                    return Response.status(Response.Status.BAD_REQUEST)
                            .header("Access-Control-Expose-Headers", "*")
                            .header("Access-Control-Allow-Origin", "*")
                            .entity(responseModel).build();
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
