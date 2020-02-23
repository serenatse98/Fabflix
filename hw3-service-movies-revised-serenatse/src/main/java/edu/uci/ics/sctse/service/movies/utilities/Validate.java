package edu.uci.ics.sctse.service.movies.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.movies.MovieService;
import edu.uci.ics.sctse.service.movies.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.movies.logger.ServiceLogger;
import edu.uci.ics.sctse.service.movies.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.movies.models.VerifyPrivilegeRequestModel;
import edu.uci.ics.sctse.service.movies.models.VerifyPrivilegeResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static edu.uci.ics.sctse.service.movies.MovieService.*;
import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.USER_PRIVILEGE_BAD;
import static edu.uci.ics.sctse.service.movies.utilities.ResultCodes.USER_PRIVILEGE_GOOD;

public class Validate
{
    private static int isUserAllowedToMakeRequest(String email, int lowestPlevel)
    {
        ServiceLogger.LOGGER.info("Verifying privilege level with IDM...");

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Get the URI for the IDM
        ServiceLogger.LOGGER.info("Building URI...");
        String IDM_URI = MovieService.getIdmConfigs().getIdmUri();
        ServiceLogger.LOGGER.info("URI = " + IDM_URI);

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String IDM_ENDPOINT_PATH = MovieService.getIdmConfigs().getPrivilegePath();

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Set the payload
        ServiceLogger.LOGGER.info("Setting payload of the request");
        VerifyPrivilegeRequestModel requestModel = new VerifyPrivilegeRequestModel(email, lowestPlevel);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");

        GeneralResponseModel responseModel = null;

        // Check that status code of the request
        if (response.getStatus() == 200) {
            ServiceLogger.LOGGER.info("Received Status 200");
            // Success! Map the response to a ResponseModel
            String jsonText = response.readEntity(String.class);
            ServiceLogger.LOGGER.info("JsonText: " + jsonText);

            ObjectMapper mapper = new ObjectMapper();
            try
            {
                responseModel = mapper.readValue(jsonText, GeneralResponseModel.class);
            }
            catch (IOException e)
            {
                ServiceLogger.LOGGER.warning(ANSI_RED + "IO Exception in Validate" + ANSI_RESET);
            }

            ServiceLogger.LOGGER.info(ANSI_PURPLE + "STATUS: " + responseModel.getResultCode() + ANSI_RESET);
            return responseModel.getResultCode();
        }
        else {
            ServiceLogger.LOGGER.info("Received Status " + response.getStatus());
        }
        return 0;
    }

    public static Response returnNoPrivilegeResponse()
    {
        VerifyPrivilegeResponseModel privilegeResponseModel;
        privilegeResponseModel  = new VerifyPrivilegeResponseModel(USER_PRIVILEGE_BAD);
        return Response.status(Response.Status.OK).entity(privilegeResponseModel).build();
    }

    public static boolean hasPrivilege(String email, int plevel)
    {
//        try
//        {
            return isUserAllowedToMakeRequest(email, plevel) == USER_PRIVILEGE_GOOD;
//        }
//        catch (IOException e)
//        {
//            String warning = "IOException while verifying privilege.";
//            ServiceLogger.LOGGER.warning(ANSI_RED + warning + ANSI_RESET);
//            throw new ModelValidationException(warning, e);
//        }
    }
}
