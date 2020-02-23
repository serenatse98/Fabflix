package edu.uci.ics.sctse.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.idm.core.ResultCodes;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.models.PrivilegeRequestModel;
import edu.uci.ics.sctse.service.idm.models.PrivilegeResponseModel;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.sql.SQLException;

@Path("privilege")
public class Privilege
{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userPrivilege(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to validate user privileges");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        ObjectMapper mapper = new ObjectMapper();
        PrivilegeRequestModel requestModel;
        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, PrivilegeRequestModel.class);

            ServiceLogger.LOGGER.info("Getting result code...");
            int resultCode = requestModel.resultCode();
            ServiceLogger.LOGGER.info("Result code: " + resultCode);
            String message = ResultCodes.resultMessage(resultCode);

            ResultCodes resultResponse = new ResultCodes(resultCode);
            PrivilegeResponseModel responseModel = new PrivilegeResponseModel(resultCode, message);
            ServiceLogger.LOGGER.info(responseModel.toString());

            String response = resultResponse.getResponse(resultCode);
            ServiceLogger.LOGGER.info("Response: " + response);
            switch (response)
            {
                case "ok":
                    return Response.status(Status.OK).entity(responseModel).build();
                case "bad":
                    return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
                case "error":
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }
        catch (SQLException sqle)
        {
            ServiceLogger.LOGGER.warning("SQL Exception.");
            sqle.printStackTrace();
            PrivilegeResponseModel responseModel = new PrivilegeResponseModel(-1, "Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
        catch (IOException e)
        {

            if (e instanceof JsonMappingException)
            {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                ServiceLogger.LOGGER.info("JSON not in correct format.");
                PrivilegeResponseModel responseModel = new PrivilegeResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException)
            {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                ServiceLogger.LOGGER.info("JSON not in correct format..");
                PrivilegeResponseModel responseModel = new PrivilegeResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else
            {
                ServiceLogger.LOGGER.warning("IOException. Internal Server Error");
                PrivilegeResponseModel responseModel = new PrivilegeResponseModel(-1, "Internal server error.");
                e.printStackTrace();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        // SHOULD NOT REACH HERE
        ServiceLogger.LOGGER.warning("should NOT reach this...");
        return Response.status(Status.NO_CONTENT).build();
    }

}
