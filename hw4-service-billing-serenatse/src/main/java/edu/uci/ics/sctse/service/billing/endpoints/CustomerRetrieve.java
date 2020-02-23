package edu.uci.ics.sctse.service.billing.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.billing.core.ResultCodes;
import edu.uci.ics.sctse.service.billing.database.CustomerDatabase;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.models.CustomerRetrieveRequestModel;
import edu.uci.ics.sctse.service.billing.models.CustomerRetrieveResponseModel;
import edu.uci.ics.sctse.service.billing.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.billing.objects.Customer;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("customer")
public class CustomerRetrieve
{
    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomer(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to retrieve customer");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        ObjectMapper mapper = new ObjectMapper();
        CustomerRetrieveRequestModel requestModel;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, CustomerRetrieveRequestModel.class);
            ServiceLogger.LOGGER.info("Mapped to JSON");


            int resultCode = 0;
            String message;

            try
            {
                if (!CustomerDatabase.isCustomerInDB(requestModel.getEmail()))
                {
                    ServiceLogger.LOGGER.info("Customer not in DB");
                    resultCode = 332;
                    message = ResultCodes.resultMessage(resultCode);
                    GeneralResponseModel responseModel = new GeneralResponseModel(resultCode, message);
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }
                else
                {
                    ServiceLogger.LOGGER.info("Getting customer info");
                    Customer customer = CustomerDatabase.getCustomerFromDB(requestModel.getEmail());
                    resultCode = 3320;
                    message = ResultCodes.resultMessage(resultCode);
                    CustomerRetrieveResponseModel responseModel =
                            new CustomerRetrieveResponseModel(resultCode, message, customer);
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                }
            }
            catch (SQLException e)
            {
                ServiceLogger.LOGGER.info("SQL Exception: " + e.getErrorCode());
                e.printStackTrace();
//                resultCode = -1;
//                message = ResultCodes.resultMessage(resultCode);
//                GeneralResponseModel responseModel = new GeneralResponseModel(resultCode, message);
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
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
