package edu.uci.ics.sctse.service.api_gateway.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.api_gateway.GatewayService;
import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.sctse.service.api_gateway.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.VerifySessionRequestModel;
import edu.uci.ics.sctse.service.api_gateway.models.VerifySessionResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.idm.SessionRequestModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static edu.uci.ics.sctse.service.api_gateway.GatewayService.*;

public class Validate
{
    public static int getSessionValidation(String email, String sessionID)
    {
        ServiceLogger.LOGGER.info("Verifying privilege level with IDM...");

        // Create a new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Get the URI for the IDM
        ServiceLogger.LOGGER.info("Building URI...");
        String IDM_URI = GatewayService.getIdmConfigs().getIdmUri();
        ServiceLogger.LOGGER.info("URI = " + IDM_URI);

        ServiceLogger.LOGGER.info("Setting path to endpoint...");
        String IDM_ENDPOINT_PATH = GatewayService.getIdmConfigs().getEPSessionVerify();

        // Create a WebTarget to send a request at
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(IDM_URI).path(IDM_ENDPOINT_PATH);

        // Create an InvocationBuilder to create the HTTP request
        ServiceLogger.LOGGER.info("Starting invocation builder...");
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        // Set the payload
        ServiceLogger.LOGGER.info("Setting payload of the request");
        VerifySessionRequestModel requestModel = new VerifySessionRequestModel(email, sessionID);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
        ServiceLogger.LOGGER.info("Sent!");

        ServiceLogger.LOGGER.info(ANSI_PURPLE + "STATUS RETURNED: " + ANSI_RESET);

        VerifySessionResponseModel responseModel = null;

        // Check that status code of the request
        if (response.getStatus() == 200) {
            ServiceLogger.LOGGER.info("Received Status 200");
            // Success! Map the response to a ResponseModel
            String jsonText = response.readEntity(String.class);
            ServiceLogger.LOGGER.info("JsonText: " + jsonText);

            ObjectMapper mapper = new ObjectMapper();
            try
            {
                responseModel = mapper.readValue(jsonText, VerifySessionResponseModel.class);
            }
            catch (IOException e)
            {
                ServiceLogger.LOGGER.warning(ANSI_RED + "IO Exception in Validate" + ANSI_RESET);
                e.printStackTrace();
            }

            ServiceLogger.LOGGER.info(ANSI_PURPLE + "STATUS: " + responseModel.getResultCode() + ANSI_RESET);
            return responseModel.getResultCode();
        }
        else {
            ServiceLogger.LOGGER.info("Received Status " + response.getStatus());
        }

        return 0;
    }
}

