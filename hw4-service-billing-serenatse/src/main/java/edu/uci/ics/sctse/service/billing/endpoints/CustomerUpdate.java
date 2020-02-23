package edu.uci.ics.sctse.service.billing.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.billing.core.ResultCodes;
import edu.uci.ics.sctse.service.billing.core.Validate;
import edu.uci.ics.sctse.service.billing.database.CreditcardDatabase;
import edu.uci.ics.sctse.service.billing.database.CustomerDatabase;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.models.CustomerUpdateRequestModel;
import edu.uci.ics.sctse.service.billing.models.GeneralResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("customer")
public class CustomerUpdate
{
    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to update customer");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        ObjectMapper mapper = new ObjectMapper();
        CustomerUpdateRequestModel requestModel;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, CustomerUpdateRequestModel.class);
            ServiceLogger.LOGGER.info("Mapped to JSON");

            GeneralResponseModel responseModel;
            int resultCode = 0;

            if (!Validate.hasValidCreditcardLen(requestModel.getCcid()))
            {
                ServiceLogger.LOGGER.info("Creditcard ID length invalid");
                resultCode = 321;
            }
            else if (!Validate.isValidCreditcardID(requestModel.getCcid()))
            {
                ServiceLogger.LOGGER.info("Creditcard is not all digits.");
                resultCode = 322;
            }
            else
            {
                try
                {
                    if (!CreditcardDatabase.isCCinDB(requestModel.getCcid()))
                    {
                        ServiceLogger.LOGGER.info("Creditcard not found");
                        resultCode = 331;
                    }
                    else if (!CustomerDatabase.isCustomerInDB(requestModel.getEmail()))
                    {
                        ServiceLogger.LOGGER.info("Email not in customer DB");
                        resultCode = 332;
                    }
                    else
                    {
                        ServiceLogger.LOGGER.info("Update customer");
                        CustomerDatabase.updateCustomerInDB(requestModel.getEmail(), requestModel.getFirstName(),
                                requestModel.getLastName(), requestModel.getCcid(), requestModel.getAddress());
                        resultCode = 3310;
                    }
                }
                catch (SQLException e)
                {
                    ServiceLogger.LOGGER.info("SQL Exception: " + e.getErrorCode());
                    e.printStackTrace();
                    resultCode = -1;
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
