package edu.uci.ics.sctse.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.idm.core.SessionDatabase;
import edu.uci.ics.sctse.service.idm.core.UserDatabase;
import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;
import edu.uci.ics.sctse.service.idm.models.SessionRequestModel;
import edu.uci.ics.sctse.service.idm.models.SessionResponseModel;
import edu.uci.ics.sctse.service.idm.utilities.HTTPStatusCodes;
import edu.uci.ics.sctse.service.idm.utilities.ResultCodes;
import edu.uci.ics.sctse.service.idm.validation.PassEmailChecker;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("session")
public class VerifySession
{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySession(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to verify user's session");
        ServiceLogger.LOGGER.info("Request:\n" + jsonText);

        ObjectMapper mapper = new ObjectMapper();
        SessionRequestModel requestModel;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, SessionRequestModel.class);
            ServiceLogger.LOGGER.info("Mapped to JSON");

            SessionResponseModel responseModel;
            int resultCode;
            String email = requestModel.getEmail();
            String sessionID = requestModel.getSessionID();

            if (sessionID.length() <= 0)
            {
                ServiceLogger.LOGGER.info("Session has invalid length");
                resultCode = -13;
            }
            else if (!PassEmailChecker.hasValidEmail(email))
            {
                ServiceLogger.LOGGER.info("Invalid email format");
                resultCode = -11;
            }
            else if (email.length() > 50)
            {
                ServiceLogger.LOGGER.info("Invalid email length");
                resultCode = -10;
            }
            else if (!UserDatabase.isEmailInDatabase(email) || !SessionDatabase.isEmailInDB(email))
            {
                ServiceLogger.LOGGER.info("Email not in DB");
                resultCode = 14;
            }
            else
            {
                int SESSION_ACTIVE = 1;
                int SESSION_CLOSED = 2;
                int SESSION_EXPIRED = 3;
                int SESSION_REVOKED = 4;

                int sessionStatus = SessionDatabase.checkStatus(email, sessionID);//SessionDatabase.getSessionStatus(email, sessionID);
                if (sessionStatus == SESSION_ACTIVE)
                {
                    ServiceLogger.LOGGER.info("Session is active");
                    resultCode = 130;
                    responseModel = new SessionResponseModel(resultCode, ResultCodes.getMessage(resultCode), sessionID);
                    return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();
                }
                else if (sessionStatus == SESSION_CLOSED)
                {
                    ServiceLogger.LOGGER.info("Session is closed.");
                    resultCode= 132;
                }
                else if (sessionStatus == SESSION_EXPIRED)
                {
                    ServiceLogger.LOGGER.info("Session is expired");
                    resultCode = 131;
                }
                else if (sessionStatus == SESSION_REVOKED)
                {
                    ServiceLogger.LOGGER.info("Session is revoked.");
                    resultCode = 133;
                }
                else
                {
                    ServiceLogger.LOGGER.info("Session not found.");
                    resultCode = 134;
                }

            }

            responseModel = new SessionResponseModel(resultCode, ResultCodes.getMessage(resultCode), null);
            return Response.status(HTTPStatusCodes.setHTTPStatus(resultCode)).entity(responseModel).build();

        }
        catch (SQLException sqle)
        {
            ServiceLogger.LOGGER.warning("SQL exception");
            sqle.printStackTrace();
            SessionResponseModel responseModel = new SessionResponseModel(-1, "Internal server error.", null);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
        catch (IOException e)
        {
            if (e instanceof JsonMappingException)
            {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                ServiceLogger.LOGGER.info("JSON not in correct format.");
                SessionResponseModel responseModel = new SessionResponseModel(-2, "JSON Mapping Exception.", null);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException)
            {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                ServiceLogger.LOGGER.info("JSON not in correct format..");
                SessionResponseModel responseModel = new SessionResponseModel(-3, "JSON Parse Exception.", null);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }
            else
            {
                ServiceLogger.LOGGER.warning("IOException. Internal Server Error");
                e.printStackTrace();
                SessionResponseModel responseModel = new SessionResponseModel(-1, "Internal server error.", null);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();            }
        }

    }
}
