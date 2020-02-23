package edu.uci.ics.sctse.service.api_gateway.resources;

import edu.uci.ics.sctse.service.api_gateway.database.ResponseDatabase;
import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

import static edu.uci.ics.sctse.service.api_gateway.GatewayService.ANSI_RED;
import static edu.uci.ics.sctse.service.api_gateway.GatewayService.ANSI_RESET;

@Path("report")
public class Report
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReport(@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Got request for report.");

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionid");

        Map<String, Object> response;
        Object httpstatus;
        do
        {
            response = ResponseDatabase.getResponseFromDB(transactionID);
            httpstatus = response.get("httpstatus");
            if (transactionID == null)
            {
                break;
            }
        }
        while (httpstatus == null);


//        if ((int)httpstatus != 200)
//        {
//            ResponseDatabase.deleteResponse(transactionID);
//            return Response.status(Response.Status.NO_CONTENT)
//                    .header("email", email)
//                    .header("sessionID", sessionID)
//                    .header("transactionID", transactionID)
//                    .header("Access-Control-Expose-Headers", "*")
//                    .header("Access-Control-Allow-Origin", "*")
//                    .entity(response.get("response"))
//                    .build();
//        }

        ResponseDatabase.deleteResponse(transactionID);
        return Response.status((int)httpstatus)
                .header("email", email)
                .header("sessionID", sessionID)
                .header("transactionID", transactionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .entity(response.get("response"))
                .build();
    }
}
