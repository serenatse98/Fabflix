package edu.uci.ics.sctse.service.api_gateway.resources;

import edu.uci.ics.sctse.service.api_gateway.GatewayService;
import edu.uci.ics.sctse.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.sctse.service.api_gateway.models.*;
import edu.uci.ics.sctse.service.api_gateway.models.idm.LoginUserRequestModel;
import edu.uci.ics.sctse.service.api_gateway.models.idm.PrivilegeRequestModel;
import edu.uci.ics.sctse.service.api_gateway.models.idm.RegisterUserRequestModel;
import edu.uci.ics.sctse.service.api_gateway.models.idm.SessionRequestModel;
import edu.uci.ics.sctse.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.sctse.service.api_gateway.utilities.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static edu.uci.ics.sctse.service.api_gateway.utilities.ResultCodes.SESSION_ACTIVE;

@Path("idm")
public class IDMEndpoints {
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUserRequest(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to register user.");
        RegisterUserRequestModel requestModel;

        // Map jsonText to RequestModel
        try
        {
            requestModel = (RegisterUserRequestModel) ModelValidator.verifyModel(jsonText, RegisterUserRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserRegister());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
//        // set the email ??????????????????? pizza @1131
//        cr.setEmail(requestModel.getEmail());
        // set http method type
        cr.setHttpMethodType(Constants.postRequest);
        ServiceLogger.LOGGER.info("Created & set client request: " + cr.toString());

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        ServiceLogger.LOGGER.info("Enqueuing request...");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Request in queue.");

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT)
                .header("transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUserRequest(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to register user.");
        LoginUserRequestModel requestModel;

        // Map jsonText to RequestModel
        try
        {
            requestModel = (LoginUserRequestModel) ModelValidator.verifyModel(jsonText, LoginUserRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserLogin());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
//        // set the email???????
//        cr.setEmail(requestModel.getEmail());
//        // set SessionID?????
//        String response = ResponseDatabase.getResponseFromDB(transactionID);
//        ServiceLogger.LOGGER.info("RESPONSE: " + response);
//        cr.setSessionID();
        // set http method type
        cr.setHttpMethodType(Constants.postRequest);
        ServiceLogger.LOGGER.info("Created & set client request: " + cr.toString());

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        ServiceLogger.LOGGER.info("Enqueuing request...");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Request in queue.");

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT)
                .header("transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("session")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySessionRequest(String jsonText) {
        ServiceLogger.LOGGER.info("Received request to register user.");
        SessionRequestModel requestModel;

        // Map jsonText to RequestModel
        try
        {
            requestModel = (SessionRequestModel) ModelValidator.verifyModel(jsonText, SessionRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPSessionVerify());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set http method type
        cr.setHttpMethodType(Constants.postRequest);
        ServiceLogger.LOGGER.info("Created & set client request: " + cr.toString());

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        ServiceLogger.LOGGER.info("Enqueuing request...");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Request in queue.");

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT)
                .header("transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("privilege")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyUserPrivilegeRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to register user.");

        String email = headers.getHeaderString("email");
        if (email == null)
        {
            VerifyEmailResponseModel responseModel = new VerifyEmailResponseModel(-16);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        String sessionID = headers.getHeaderString("sessionID");
        if (sessionID == null)
        {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, sessionID);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        int sessionStatus = Validate.getSessionValidation(email, sessionID);
        if (sessionStatus != SESSION_ACTIVE)
        {
            ServiceLogger.LOGGER.info("sessionSTATUS: " + sessionStatus);
//            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(sessionStatus, sessionID);
            GeneralResponseModel responseModel = new GeneralResponseModel(sessionStatus);
            return Response.status(Status.OK).entity(responseModel).build();
        }

        PrivilegeRequestModel requestModel;

        // Map jsonText to RequestModel
        try
        {
            requestModel = (PrivilegeRequestModel) ModelValidator.verifyModel(jsonText, PrivilegeRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getIdmConfigs().getIdmUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getIdmConfigs().getEPUserPrivilegeVerify());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the sessionID
        cr.setSessionID(sessionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set http method type
        cr.setHttpMethodType(Constants.postRequest);
        ServiceLogger.LOGGER.info("Created & set client request: " + cr.toString());

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        ServiceLogger.LOGGER.info("Enqueuing request...");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Request in queue.");

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT)
                .entity(responseModel)
                .header("transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }
}
