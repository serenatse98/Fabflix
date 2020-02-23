package edu.uci.ics.sctse.service.billing.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.billing.core.PayPalClient;
import edu.uci.ics.sctse.service.billing.core.ResultCodes;
import edu.uci.ics.sctse.service.billing.database.TransactionsDatabase;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.billing.models.OrderCompleteRequestModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Map;

@Path("order")
public class OrderComplete
{
    @Path("complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeOrder(
            @QueryParam("paymentId") String paymentId,
            @QueryParam("token") String token,
            @QueryParam("PayerID") String PayerID)
    {
        ServiceLogger.LOGGER.info("Completing order...");
//        String paymentId = headers.getHeaderString("paymentId");
//        String token = headers.getHeaderString("token");
//        String PayerID = headers.getHeaderString("PayerID");

        try
        {
            GeneralResponseModel responseModel;
            int resultCode;
            PayPalClient payPalClient = new PayPalClient();
            Map<String, Object> payComplete = payPalClient.completePayment(paymentId, PayerID);

            if (!TransactionsDatabase.isTokenInDB(token))
            {
                ServiceLogger.LOGGER.info("Token not found (in DB).");
                resultCode = 3421;
            }
            else if (payComplete.get("status").equals("success"))
            {
                ServiceLogger.LOGGER.info("Payment completion success");
                String tID = payComplete.get("transactionID").toString();
                ServiceLogger.LOGGER.info("TRANSACTION ID: " + tID);
                TransactionsDatabase.insertTransIDIntoDB(tID, token);
                resultCode = 3420;
            }
            else
            {
                ServiceLogger.LOGGER.info("Payment completion failed");
                resultCode = 3422;
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
