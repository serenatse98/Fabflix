package edu.uci.ics.sctse.service.idm.resources;

import edu.uci.ics.sctse.service.idm.logger.ServiceLogger;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("test")
public class TestPage {
    @Path("hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello(@Context HttpHeaders headers) {
        String receivedMessage = headers.getHeaderString("message");
        String sender = headers.getHeaderString("sender");
        ServiceLogger.LOGGER.info(receivedMessage);
        String responseMessage = "IDM: Hello, " + sender + "!";
        return Response.status(Status.OK).entity(responseMessage).build();
    }
}
