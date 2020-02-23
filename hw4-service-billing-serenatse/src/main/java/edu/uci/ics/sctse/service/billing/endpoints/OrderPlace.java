package edu.uci.ics.sctse.service.billing.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.Transaction;
import edu.uci.ics.sctse.service.billing.core.PayPalClient;
import edu.uci.ics.sctse.service.billing.core.ResultCodes;
import edu.uci.ics.sctse.service.billing.database.CartDatabase;
import edu.uci.ics.sctse.service.billing.database.CustomerDatabase;
import edu.uci.ics.sctse.service.billing.database.SalesDatabase;
import edu.uci.ics.sctse.service.billing.database.TransactionsDatabase;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.billing.models.OrderPlaceRequestModel;
import edu.uci.ics.sctse.service.billing.models.OrderPlaceResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Path("order")
public class OrderPlace
{
    @Path("place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrder(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to place order");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        ObjectMapper mapper = new ObjectMapper();
        OrderPlaceRequestModel requestModel;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, OrderPlaceRequestModel.class);
            ServiceLogger.LOGGER.info("Mapped to JSON");

            GeneralResponseModel responseModel;
            int resultCode = 0;

//            if (!CustomerDatabase.isCustomerInDB(requestModel.getEmail()))
//            {
//                ServiceLogger.LOGGER.info("customer not in customer DB");
//                resultCode = 332;
//            }
//            else
            if (!CartDatabase.isEmailInCartsDB(requestModel.getEmail()))
            {
                ServiceLogger.LOGGER.info("Customer's shopping cart not found/empty");
                resultCode = 341;
            }
            String message = ResultCodes.resultMessage(resultCode);
            responseModel = new GeneralResponseModel(resultCode, message);

            if (resultCode == 0)
            {

                ServiceLogger.LOGGER.info("placing order.");
                String sum = CartDatabase.getSumOfItems(requestModel.getEmail());
//                SalesDatabase.insertOrderInDB(requestModel.getEmail());

                ServiceLogger.LOGGER.info("SUM AFTER ROUNDING: " + sum);
                PayPalClient payPalClient = new PayPalClient();
                Map<String, Object> response = payPalClient.createPayment(sum);

//                ServiceLogger.LOGGER.info("RESPONSE: " + response.);

                if (response.get("status").equals("success"))
                {
                    ServiceLogger.LOGGER.info("Payment created successfully");
                    String redirectUrl = response.get("redirectUrl").toString();
                    ServiceLogger.LOGGER.info("REDIRECT URL: " + redirectUrl);
                    String token = redirectUrl.substring(redirectUrl.indexOf("token") + 6);
                    SalesDatabase.insertOrderInDB(requestModel.getEmail(), token);
//                    TransactionsDatabase.insertTokenIntoDB(requestModel.getEmail(), token);

                    resultCode = 3400;
                    message = ResultCodes.resultMessage(resultCode);
                    OrderPlaceResponseModel OPResponseModel =
                            new OrderPlaceResponseModel(resultCode, message, redirectUrl, token);
                    return Response.status(Response.Status.OK).entity(OPResponseModel).build();
                }
                else //if (response.get("status").equals("fail"))
                {
                    ServiceLogger.LOGGER.info("Payment did not succeed/create.");
                    resultCode = 342;
                    message = ResultCodes.resultMessage(resultCode);
                    responseModel = new GeneralResponseModel(resultCode, message);
                }
            }

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
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.info("SQL Exception: " + e.getErrorCode());
            e.printStackTrace();
            GeneralResponseModel responseModel = new GeneralResponseModel(-1, "Internal server error.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }

        // SHOULD NOT REACH HERE
        ServiceLogger.LOGGER.warning("should NOT reach this...");
        GeneralResponseModel serverError = new GeneralResponseModel(-1, "Internal Server Error.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(serverError).build();
    }

}
