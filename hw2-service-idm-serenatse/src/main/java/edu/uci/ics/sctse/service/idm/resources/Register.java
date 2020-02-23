package edu.uci.ics.sctse.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.idm.core.ResultCodes;
import edu.uci.ics.sctse.service.idm.core.UserDatabase;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.models.RegisterRequestModel;
import edu.uci.ics.sctse.service.idm.models.RegisterResponseModel;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.sql.SQLException;

@Path("register")
public class Register
{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to register user");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        ObjectMapper mapper = new ObjectMapper();
        RegisterRequestModel rrm;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            rrm = mapper.readValue(jsonText, RegisterRequestModel.class);

            ServiceLogger.LOGGER.info("Validating email and password...");
            rrm.checkEmailAndPassword();
            int resultCode = rrm.resultCode();
            ServiceLogger.LOGGER.info("Result code: " + resultCode);
            String message = ResultCodes.resultMessage(resultCode);


            ResultCodes resultResponse = new ResultCodes(resultCode);

            RegisterResponseModel responseModel = new RegisterResponseModel(resultCode, message);
            ServiceLogger.LOGGER.info(responseModel.toString());


            if (resultCode == 110)
            {
                ServiceLogger.LOGGER.info("Initializing database insertion");
                ServiceLogger.LOGGER.info("SALTTTT " + rrm.getSalt());
                UserDatabase.insertUserIntoDB(rrm);
            }

            if (resultResponse.getResponse(resultCode).equals("bad"))
            {
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }

            else if (resultResponse.getResponse(resultCode).equals("ok"))
            {
                return Response.status(Status.OK).entity(responseModel).build();
            }

            else if (resultResponse.getResponse(resultCode).equals("error"))
            {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }

        }
        catch (SQLException sqle)
        {
            ServiceLogger.LOGGER.warning("SQL Exception.");
            sqle.printStackTrace();
            RegisterResponseModel responseModel = new RegisterResponseModel(-1, "Internal server error.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
        catch (IOException e)
        {

            if (e instanceof JsonMappingException)
            {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                ServiceLogger.LOGGER.info("JSON not in correct format.");
                RegisterResponseModel responseModel = new RegisterResponseModel(-2, "JSON Mapping Exception.");
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException)
            {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                ServiceLogger.LOGGER.info("JSON not in correct format..");
                RegisterResponseModel responseModel = new RegisterResponseModel(-3, "JSON Parse Exception.");
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else
            {
                ServiceLogger.LOGGER.warning("IOException. Internal Server Error");
                RegisterResponseModel responseModel = new RegisterResponseModel(-1, "Internal server error.");
                e.printStackTrace();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        // SHOULD NOT REACH HERE
        ServiceLogger.LOGGER.warning("should NOT reach this...");
        return Response.status(Status.NO_CONTENT).build();
    }
}
