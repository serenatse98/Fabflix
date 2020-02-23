package edu.uci.ics.sctse.service.billing.endpoints;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.billing.core.ResultCodes;
import edu.uci.ics.sctse.service.billing.core.Validate;
import edu.uci.ics.sctse.service.billing.database.CreditcardDatabase;
import edu.uci.ics.sctse.service.billing.logger.ServiceLogger;
import edu.uci.ics.sctse.service.billing.models.CreditcardRetrieveRequestModel;
import edu.uci.ics.sctse.service.billing.models.CreditcardRetrieveResponseModel;
import edu.uci.ics.sctse.service.billing.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.billing.objects.Creditcard;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;


@Path("creditcard")
public class CreditcardRetrieve
{
    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditcard(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to retrieve creditcard");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        ObjectMapper mapper = new ObjectMapper();
        CreditcardRetrieveRequestModel requestModel;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, CreditcardRetrieveRequestModel.class);
            ServiceLogger.LOGGER.info("Mapped to JSON");

            GeneralResponseModel responseModel;
            int resultCode = 0;

            if (!Validate.hasValidCreditcardLen(requestModel.getId()))
            {
                ServiceLogger.LOGGER.info("Creditcard ID length invalid");
                resultCode = 321;
            }
            else if (!Validate.isValidCreditcardID(requestModel.getId()))
            {
                ServiceLogger.LOGGER.info("Creditcard is not all digits.");
                resultCode = 322;
            }
            String message = ResultCodes.resultMessage(resultCode);
            responseModel = new GeneralResponseModel(resultCode, message);

            if (resultCode == 0)
            {
                try
                {
                    if (!CreditcardDatabase.isCCinDB(requestModel.getId()))
                    {
                        ServiceLogger.LOGGER.info("Creditcard not in DB.");
                        resultCode = 324;
                        message = ResultCodes.resultMessage(resultCode);
                        responseModel = new GeneralResponseModel(resultCode, message);
                    }
                    else
                    {
                        ServiceLogger.LOGGER.info("Retrieving creditcard info");
                        Creditcard creditcard = CreditcardDatabase.getCreditcardInfo(requestModel.getId());
                        resultCode = 3230;
                        message = ResultCodes.resultMessage(resultCode);
                        responseModel = new CreditcardRetrieveResponseModel(resultCode, message, creditcard);
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
