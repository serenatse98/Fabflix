package edu.uci.ics.sctse.service.billing.endpoints;

import com.paypal.api.payments.Sale;
import edu.uci.ics.sctse.service.billing.core.PayPalClient;
import edu.uci.ics.sctse.service.billing.core.ResultCodes;
import edu.uci.ics.sctse.service.billing.database.CustomerDatabase;
import edu.uci.ics.sctse.service.billing.database.SalesDatabase;
import edu.uci.ics.sctse.service.billing.database.TransactionsDatabase;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.billing.models.OrderRetrieveResponseModel;
import edu.uci.ics.sctse.service.billing.objects.Amount;
import edu.uci.ics.sctse.service.billing.objects.Order;
import edu.uci.ics.sctse.service.billing.objects.Transaction;
import edu.uci.ics.sctse.service.billing.objects.TransactionFee;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("order")
public class OrderRetrieve
{
    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOrder(@Context HttpHeaders headers, @QueryParam("email") String em)//String jsonText)
    {

        ServiceLogger.LOGGER.info("Received request to retrieve orders");

        String email=headers.getHeaderString("email");
        ServiceLogger.LOGGER.info("Email: " + email);
//        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

//        ObjectMapper mapper = new ObjectMapper();
//        OrderRetrieveRequestModel requestModel;

        try
        {
//            ServiceLogger.LOGGER.info("Mapping JSON to request model");
//            requestModel = mapper.readValue(jsonText, OrderRetrieveRequestModel.class);
//            ServiceLogger.LOGGER.info("Mapped to JSON");

            GeneralResponseModel responseModel;
            int resultCode = 0;


//            if (!CustomerDatabase.isCustomerInDB(email))//requestModel.getEmail()))
//            {
//                ServiceLogger.LOGGER.info("customer not in customer DB");
//                resultCode = 332;
//            }
//
//            else
//            {
                ServiceLogger.LOGGER.info("Retrieving order info");

                ArrayList<Transaction> transactionArrayList = new ArrayList<>();
                ArrayList<String> tIDs = TransactionsDatabase.getTransID(email);
                PayPalClient payPalClient = new PayPalClient();
                for (String tID : tIDs)
                {
                    if (tID == null)
                    {
                        ServiceLogger.LOGGER.info("TID IS NULL!");
//
                    }
                    else
                    {
                        Sale sale = payPalClient.retrievePayment(tID);
                        Amount amount = new Amount(sale.getAmount().getTotal(), sale.getAmount().getCurrency());
                        TransactionFee transactionFee = new TransactionFee(
                                sale.getTransactionFee().getValue(), sale.getTransactionFee().getCurrency());
                        Order[] items = SalesDatabase.getOrderItemsFromDB(email, tID);//requestModel.getEmail());
                        Transaction transaction = new Transaction(
                                sale.getId(), sale.getState(), amount, transactionFee, sale.getCreateTime(), sale.getUpdateTime(), items);
                        transactionArrayList.add(transaction);
                    }
                }

                resultCode = 3410;
                String mes = ResultCodes.resultMessage(resultCode);
                OrderRetrieveResponseModel rm =
                        new OrderRetrieveResponseModel(resultCode, mes, transactionArrayList);
                return Response.status(Response.Status.OK).entity(rm).build();
//            }



//            String message = ResultCodes.resultMessage(resultCode);
//            responseModel = new GeneralResponseModel(resultCode, message);

//            String response = ResultCodes.getResponse(resultCode);
//            switch (response)
//            {
//                case "200 OK":
//                    return Response.status(Response.Status.OK).entity(responseModel).build();
//                case "400 Bad Request":
//                    return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
//                case "500 Internal Server Error":
//                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
//            }

        }
        catch (SQLException e)
        {
            ServiceLogger.LOGGER.info("SQL Exception: " + e.getErrorCode());
            e.printStackTrace();
            GeneralResponseModel responseModel = new GeneralResponseModel(-1, "Internal Server Error.");
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
//        catch (IOException e)
//        {
//            if (e instanceof JsonMappingException)
//            {
//                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
//                ServiceLogger.LOGGER.info("JSON not in correct format.");
//                e.printStackTrace();
//                GeneralResponseModel responseModel = new GeneralResponseModel(-2, "JSON Mapping Exception.");
//                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
//            }
//            else if (e instanceof JsonParseException)
//            {
//                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
//                ServiceLogger.LOGGER.info("JSON not in correct format..");
//                GeneralResponseModel responseModel = new GeneralResponseModel(-3, "JSON Parse Exception.");
//                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
//            }
//            else
//            {
//                ServiceLogger.LOGGER.warning("IOException. Internal Server Error");
//                e.printStackTrace();
//                GeneralResponseModel responseModel = new GeneralResponseModel(-1, "Internal server error.");
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
//            }
//        }

        // SHOULD NOT REACH HERE
//        ServiceLogger.LOGGER.warning("should NOT reach this...");
//        GeneralResponseModel serverError = new GeneralResponseModel(-1, "Internal Server Error.");
//        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(serverError).build();
    }
}
