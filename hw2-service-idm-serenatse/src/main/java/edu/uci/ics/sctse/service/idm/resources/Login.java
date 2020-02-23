package edu.uci.ics.sctse.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.idm.core.ResultCodes;
import edu.uci.ics.sctse.service.idm.core.SessionDatabase;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.models.LoginRequestModel;
import edu.uci.ics.sctse.service.idm.models.LoginResponseModel;
import edu.uci.ics.sctse.service.idm.security.Session;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.sql.SQLException;

@Path("login")
public class Login
{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to login user");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        ObjectMapper mapper = new ObjectMapper();
        LoginRequestModel requestModel;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, LoginRequestModel.class);
            ServiceLogger.LOGGER.info("Mapped to JSON");

            ServiceLogger.LOGGER.info("Getting result code...");
            int resultCode = requestModel.resultCode();
            ServiceLogger.LOGGER.info("Result code: " + resultCode);
            String message = ResultCodes.resultMessage(resultCode);

            ResultCodes resultResponse = new ResultCodes(resultCode);
            LoginResponseModel responseModel = new LoginResponseModel(resultCode, message, null);
            ServiceLogger.LOGGER.info(responseModel.toString());

            if (resultCode == 120)
            {
                ServiceLogger.LOGGER.info("Login ok - creating session...");
                Session s = Session.createSession(requestModel.getEmail());
                String sessionID = s.getSessionID().toString();
                ServiceLogger.LOGGER.info("SESSION ID: " + sessionID);

                ServiceLogger.LOGGER.info("Checking for existing/active sessions...");
                String checkSessionID = SessionDatabase.getSessionID(requestModel.getEmail());
                if (SessionDatabase.isEmailInDB(requestModel.getEmail()) &&
                        SessionDatabase.isSessionActive(requestModel.getEmail(), checkSessionID))
                {
                    ServiceLogger.LOGGER.info("Found already active session");
                    ServiceLogger.LOGGER.info("Revoking session...");
                    SessionDatabase.revokeSession(requestModel.getEmail(), checkSessionID);
                    ServiceLogger.LOGGER.info("Session revoked.");
                }

                ServiceLogger.LOGGER.info("Creating new session...");
                responseModel = new LoginResponseModel(resultCode, message, sessionID);
                SessionDatabase.insertSessionIntoDB(s);
                ServiceLogger.LOGGER.info(responseModel.toString());
            }

            String response = resultResponse.getResponse(resultCode);
            switch (response)
            {
                case "ok":
                    return Response.status(Status.OK).entity(responseModel)
                            .header("Access-Control-Expose-Headers", "*")
                            .header("Access-Control-Allow-Origin", "*")
                            .build();
                case "bad":
                    return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
                case "error":
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }


            ServiceLogger.LOGGER.info("Validating email and password...");
        }
        catch (SQLException sqle)
        {
            ServiceLogger.LOGGER.warning("SQL exception");
            sqle.printStackTrace();
            LoginResponseModel responseModel = new LoginResponseModel(-1, "Internal server error.", null);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
        catch (IOException e)
        {
            if (e instanceof JsonMappingException)
            {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                ServiceLogger.LOGGER.info("JSON not in correct format.");
                LoginResponseModel responseModel = new LoginResponseModel(-2, "JSON Mapping Exception.", null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException)
            {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                ServiceLogger.LOGGER.info("JSON not in correct format..");
                LoginResponseModel responseModel = new LoginResponseModel(-3, "JSON Parse Exception.", null);
                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else
            {
                ServiceLogger.LOGGER.warning("IOException. Internal Server Error");
                e.printStackTrace();
                LoginResponseModel responseModel = new LoginResponseModel(-1, "Internal server error.", null);
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
            }
        }

        // SHOULD NOT REACH HERE
        ServiceLogger.LOGGER.warning("should NOT reach this...");
        return Response.status(Status.NO_CONTENT).build();
    }
}
