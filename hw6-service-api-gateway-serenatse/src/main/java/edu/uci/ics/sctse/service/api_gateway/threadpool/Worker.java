package edu.uci.ics.sctse.service.api_gateway.threadpool;

import edu.uci.ics.sctse.service.api_gateway.database.ResponseDatabase;
import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.sctse.service.api_gateway.utilities.Constants;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

import static edu.uci.ics.sctse.service.api_gateway.GatewayService.*;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    private Worker(int id, ThreadPool threadPool)
    {
        this.id = id;
        this.threadPool = threadPool;
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool)
    {
        Worker newWorker = new Worker(id, threadPool);
        return newWorker;
    }

    // grab a ClientRequest from the threadPool queue.
    // If it's null, skip the rest
    // Fire off the REST call to your other services, using the URI/endpoint in the ClientRequest object
    // (just like how we have to call VerifyPrivilege in HW3, see this activity)
    // Grab a connection from the connection pool
    // Insert the response from step 3 into the responses table
    public void process()
    {
        ClientRequest clientRequest = threadPool.remove();

        if (clientRequest == null)
        {
            return;
        }


        // Do the work
        // Create new Client
        ServiceLogger.LOGGER.info("Building client...");
        Client client = ClientBuilder.newClient();
        client.register(JacksonFeature.class);

        // Get uri
        ServiceLogger.LOGGER.info("Getting URI...");
        String URI = clientRequest.getURI();

        ServiceLogger.LOGGER.info("Getting endpoint...");
        String endpointPath = clientRequest.getEndpoint();

        // Create WebTarget
        ServiceLogger.LOGGER.info("Building WebTarget...");
        WebTarget webTarget = client.target(URI).path(endpointPath);

        // Send the request and save it to a Response
        ServiceLogger.LOGGER.info("Sending request...");
        Response response = null;
        if (clientRequest.getHttpMethodType() == Constants.getRequest)
        {
            // Create InvocationBuilder
            ServiceLogger.LOGGER.info("Starting invocation builder...");
            Map<String, Object> queryMap = clientRequest.getQueryParams();
            String pathParam = clientRequest.getPathParam();

            if (pathParam != null)
            {
                webTarget = webTarget.path(pathParam);
            }

            if (queryMap != null)
            {
                ServiceLogger.LOGGER.info("QUERY MAP NOT NULL.");
                for (Map.Entry<String, Object> entry : queryMap.entrySet())
                {
                    webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
                }
            }
            ServiceLogger.LOGGER.info("WEBTARGET: " + webTarget.toString());

            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

            ServiceLogger.LOGGER.info("Got a GET request.");
            response = invocationBuilder
                    .header("email", clientRequest.getEmail())
                    .header("sessionID", clientRequest.getSessionID())
                    .header("transactionID", clientRequest.getTransactionID())
                    .get();
        }
        else if (clientRequest.getHttpMethodType() == Constants.postRequest)
        {
            // Create InvocationBuilder
            ServiceLogger.LOGGER.info("Starting invocation builder...");
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            Map<String, Object> queryMap = clientRequest.getQueryParams();

            if (queryMap != null)
            {
                ServiceLogger.LOGGER.info("QUERY MAP NOT NULL.");
                for (Map.Entry<String, Object> entry : queryMap.entrySet())
                {
                    webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
                }
            }
            ServiceLogger.LOGGER.info("WEBTARGET: " + webTarget.toString());

            ServiceLogger.LOGGER.info("Got a POST request.");
            response = invocationBuilder
                    .header("email", clientRequest.getEmail())
                    .header("sessionID", clientRequest.getSessionID())
                    .header("transactionID", clientRequest.getTransactionID())
                    .post(Entity.entity(clientRequest.getRequest(), MediaType.APPLICATION_JSON));
        }
        else if (clientRequest.getHttpMethodType() == Constants.deleteRequest)
        {
            String pathParam = clientRequest.getPathParam();
            if (pathParam != null)
            {
                webTarget = webTarget.path(pathParam);
            }
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

            ServiceLogger.LOGGER.info("Got a DELETE request.");
            response = invocationBuilder
                    .header("email", clientRequest.getEmail())
                    .header("sessionID", clientRequest.getSessionID())
                    .header("transactionID", clientRequest.getTransactionID())
                    .delete();
        }
        else
        {
            ServiceLogger.LOGGER.warning(ANSI_YELLOW + "Request was not sent successfully");
        }
        ServiceLogger.LOGGER.info(ANSI_GREEN + "Sent!" + ANSI_RESET);

        // Check status code of request
        String jsonText = "";
        int httpstatus = response.getStatus();

        jsonText = response.readEntity(String.class);

        // Add response to database
        String email = clientRequest.getEmail();
        String sessionID = clientRequest.getSessionID();
        ResponseDatabase.insertResponseIntoDB(clientRequest.getTransactionID(), jsonText, email, sessionID, httpstatus);

    }

    @Override
    public void run()
    {
        while (true)
        {
            process();
        }
    }
}
