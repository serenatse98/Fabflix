package edu.uci.ics.sctse.service.api_gateway.resources;

import edu.uci.ics.sctse.service.api_gateway.GatewayService;
import edu.uci.ics.sctse.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.sctse.service.api_gateway.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.VerifyEmailResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.VerifySessionResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.billing.*;
import edu.uci.ics.sctse.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.sctse.service.api_gateway.utilities.Constants;
import edu.uci.ics.sctse.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.sctse.service.api_gateway.utilities.TransactionIDGenerator;
import edu.uci.ics.sctse.service.api_gateway.utilities.Validate;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.HashMap;
import java.util.Map;

import static edu.uci.ics.sctse.service.api_gateway.GatewayService.ANSI_RED;
import static edu.uci.ics.sctse.service.api_gateway.GatewayService.ANSI_RESET;
import static edu.uci.ics.sctse.service.api_gateway.utilities.ResultCodes.SESSION_ACTIVE;

@Path("billing")
public class BillingEndpoints {
    @Path("cart/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertToCartRequest(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to insert into cart.");

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

        CartInsertRequestModel requestModel;

//        String email = headers.getHeaderString("email");
//        String movieId = headers.getHeaderString("movieId");
//        int quantity = Integer.parseInt(headers.getHeaderString("quantity"));
//
//        String jsonText = "{\n" + "\"email\": \"" + email + "\", \"movieId\": \"" + movieId + "\", \"quantity\": " + quantity + "\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CartInsertRequestModel) ModelValidator.verifyModel(jsonText, CartInsertRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(requestModel.getEmail());
        // set sessionID from headers
        cr.setSessionID(sessionID);
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("cart/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to update cart.");

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

        CartUpdateRequestModel requestModel;

//        String email = headers.getHeaderString("email");
//        String movieId = headers.getHeaderString("movieId");
//        int quantity = Integer.parseInt(headers.getHeaderString("quantity"));
//
//        String jsonText = "{\n" + "\"email\": \"" + email + "\", \"movieId\": \"" + movieId + "\", \"quantity\": " + quantity + "\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CartUpdateRequestModel) ModelValidator.verifyModel(jsonText, CartUpdateRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(requestModel.getEmail());
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("cart/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCartRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to delete from cart.");

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

        CartDeleteRequestModel requestModel;

//        String email = headers.getHeaderString("email");
//        String movieId = headers.getHeaderString("movieId");
//
//        String jsonText = "{\n" + "\"email\": \"" + email + "\", \"movieId\": \"" + movieId + "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CartDeleteRequestModel) ModelValidator.verifyModel(jsonText, CartDeleteRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(requestModel.getEmail());
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("cart/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCartRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to retrieve cart.");

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
        ServiceLogger.LOGGER.info(ANSI_RED + "sessionSTATUS: " + sessionStatus + ANSI_RESET);
        if (sessionStatus != SESSION_ACTIVE)
        {
            ServiceLogger.LOGGER.info("sessionSTATUS: " + sessionStatus);
            GeneralResponseModel responseModel = new GeneralResponseModel(sessionStatus);
            ServiceLogger.LOGGER.info("response: " + responseModel.toString());
            return Response.status(Status.OK).entity(responseModel).build();
        }

        CartRetrieveRequestModel requestModel;

//        String email = headers.getHeaderString("email");
//
//        String jsonText = "{\n" + "\"email\": \"" + email + "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CartRetrieveRequestModel) ModelValidator.verifyModel(jsonText, CartRetrieveRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("cart/clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCartRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to clear cart.");

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

        CartClearRequestModel requestModel;

//        String email = headers.getHeaderString("email");
//
//        String jsonText = "{\n" + "\"email\": \"" + email + "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CartClearRequestModel) ModelValidator.verifyModel(jsonText, CartClearRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartClear());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("creditcard/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCreditCardRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to insert credit card.");

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

        CreditcardInsertRequestModel requestModel;

//        String id = headers.getHeaderString("id");
//        String fName = headers.getHeaderString("firstName");
//        String lName = headers.getHeaderString("lastName");
//        Date date = Date.valueOf(headers.getHeaderString("expiration"));
//
//        String jsonText = "{\n" + "\"id\": \"" + id +
//                "\", \"firstName\": \"" + fName +
//                "\", \"lastName\": \"" + lName +
//                "\", \"expiration\": \"" + date +
//                "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CreditcardInsertRequestModel) ModelValidator.verifyModel(jsonText, CreditcardInsertRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("creditcard/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCreditCardRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to update credit card.");

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

        CreditcardUpdateRequestModel requestModel;

//        String id = headers.getHeaderString("id");
//        String fName = headers.getHeaderString("firstName");
//        String lName = headers.getHeaderString("lastName");
//        Date date = Date.valueOf(headers.getHeaderString("expiration"));
//
//        String jsonText = "{\n" + "\"id\": \"" + id +
//                "\", \"firstName\": \"" + fName +
//                "\", \"lastName\": \"" + lName +
//                "\", \"expiration\": \"" + date +
//                "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CreditcardUpdateRequestModel) ModelValidator.verifyModel(jsonText, CreditcardUpdateRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("creditcard/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCardRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to delete credit card.");

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

        CreditcardDeleteRequestModel requestModel;

//        String id = headers.getHeaderString("id");
//
//        String jsonText = "{\n" + "\"id\": \"" + id + "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CreditcardDeleteRequestModel) ModelValidator.verifyModel(jsonText, CreditcardDeleteRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("creditcard/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditCardRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to retrieve credit card.");

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

        CreditcardRetrieveRequestModel requestModel;

//        String id = headers.getHeaderString("id");
//
//        String jsonText = "{\n" + "\"id\": \"" + id + "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CreditcardRetrieveRequestModel) ModelValidator.verifyModel(jsonText, CreditcardRetrieveRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("customer/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCustomerRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to insert customer.");

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

        CustomerInsertRequestModel requestModel;

//        String email = headers.getHeaderString("email");
//        String fName = headers.getHeaderString("firstName");
//        String lName = headers.getHeaderString("lastName");
//        String ccid = headers.getHeaderString("ccid");
//        String address = headers.getHeaderString("address");
//
//        String jsonText = "{\n" + "\"email\": \"" + email +
//                "\", \"firstName\": \"" + fName +
//                "\", \"lastName\": \"" + lName +
//                "\", \"ccId\": \"" + ccid +
//                "\", \"address\": \"" + address +
//                "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CustomerInsertRequestModel) ModelValidator.verifyModel(jsonText, CustomerInsertRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("customer/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomerRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to update customer.");

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

        CustomerUpdateRequestModel requestModel;

//        String email = headers.getHeaderString("email");
//        String fName = headers.getHeaderString("firstName");
//        String lName = headers.getHeaderString("lastName");
//        String ccid = headers.getHeaderString("ccid");
//        String address = headers.getHeaderString("address");
//
//        String jsonText = "{\n" + "\"email\": \"" + email +
//                "\", \"firstName\": \"" + fName +
//                "\", \"lastName\": \"" + lName +
//                "\", \"ccId\": \"" + ccid +
//                "\", \"address\": \"" + address +
//                "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CustomerUpdateRequestModel) ModelValidator.verifyModel(jsonText, CustomerUpdateRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("customer/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to retrieve customer.");

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

        CustomerRetrieveRequestModel requestModel;

//        String email = headers.getHeaderString("email");
//
//        String jsonText = "{\n" + "\"email\": \"" + email + "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (CustomerRetrieveRequestModel) ModelValidator.verifyModel(jsonText, CustomerRetrieveRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("order/place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrderRequest(@Context HttpHeaders headers, String jsonText)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to place order.");

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

        OrderPlaceRequestModel requestModel;

//        String email = headers.getHeaderString("email");
//
//        String jsonText = "{\n" + "\"email\": \"" + email + "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (OrderPlaceRequestModel) ModelValidator.verifyModel(jsonText, OrderPlaceRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderPlace());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
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
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("order/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOrderRequest(@Context HttpHeaders headers, @QueryParam("email") String queryEmail)//@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to retrieve order.");

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

        OrderRetrieveRequestModel requestModel;

//        String email = "";
//
        String jsonText = "{\n" + "\"email\": \"" + queryEmail + "\"\n}";
        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("email", queryEmail);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (OrderRetrieveRequestModel) ModelValidator.verifyModel(jsonText, OrderRetrieveRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Billing configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Billing configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email
        cr.setEmail(headers.getHeaderString("email"));
        // set http method type
        cr.setHttpMethodType(Constants.postRequest);
        // set query params
        cr.setQueryParams(queryParams);
        ServiceLogger.LOGGER.info("Created & set client request: " + cr.toString());

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        ServiceLogger.LOGGER.info("Enqueuing request...");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Request in queue.");

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("price/{movieId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrice(@Context HttpHeaders headers, @PathParam("movieId") String movieId)
    {
        ServiceLogger.LOGGER.info("Received request to get movie pricing.");

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
        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Movie configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint("/price/");
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set http method type
        cr.setHttpMethodType(Constants.getRequest);
        // set path param
        cr.setPathParam(movieId);

        ServiceLogger.LOGGER.info("Created & set client request: " + cr.toString());

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        ServiceLogger.LOGGER.info("Enqueuing request...");
        GatewayService.getThreadPool().getQueue().enqueue(cr);
        ServiceLogger.LOGGER.info("Request in queue.");

        // Generate a NoContentResponseModel to send to the client containing the time to wait before asking for the
        // requested information, and the transactionID.
        NoContentResponseModel responseModel = new NoContentResponseModel(GatewayService.getGatewayConfigs().getRequestDelay(), transactionID);
        return Response.status(Status.NO_CONTENT).header(
                "transactionID", responseModel.getTransactionID())
                .header("message", responseModel.getMessage())
                .header("requestDelay", responseModel.getDelay())
                .header("sessionID", sessionID)
                .header("Access-Control-Expose-Headers", "*")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }
}
