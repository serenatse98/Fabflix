package edu.uci.ics.sctse.service.api_gateway.resources;

import edu.uci.ics.sctse.service.api_gateway.GatewayService;
import edu.uci.ics.sctse.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.sctse.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.sctse.service.api_gateway.models.GeneralResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.NoContentResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.VerifyEmailResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.VerifySessionResponseModel;
import edu.uci.ics.sctse.service.api_gateway.models.movies.*;
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

@Path("movies")
public class MovieEndpoints {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovieRequest(
            @Context HttpHeaders headers,
            @QueryParam("title") String title,
            @QueryParam("genre") String genre,
            @QueryParam("year") int year,
            @QueryParam("director") String director,
            @QueryParam("hidden") boolean hidden,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit,
            @DefaultValue("rating") @QueryParam("orderby") String orderby,
            @DefaultValue("desc") @QueryParam("direction") String direction)
    {
        ServiceLogger.LOGGER.info("Received request to search movies.");
        SearchMovieRequestModel requestModel;

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

        limit = (limit != 10 && limit != 25 && limit != 50 && limit != 100) ? 10 : limit;
        offset = (offset < 0 || offset % limit != 0) ? 0 : offset;

        String t = (title == null) ? "" : "\t\"title\":\"" + title + "\",\n";
        String g = (genre == null) ? "" : "\t\"genre\":\"" + genre + "\",\n";
        String y = (year == 0) ? "" : "\t\"year\":" + year + ",\n";
        String d = (director == null) ? "" : "\t\"director\":\"" + director + "\",\n";
        String h = "";

        String off = "\t\"offset\":" + offset + ",\n";
        String lim = "\t\"limit\":" + limit + ",\n";
        String dir = "\t\"direction\":\"" + direction + "\",\n";
        String order = "\t\"orderby\":\"" + orderby + "\"\n";
        String jsonText = "{\n" + t + g + y + d + h + off + lim + dir + order + "}";

        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("title", title);
        queryParams.put("genre", genre);
        queryParams.put("year", year);
        queryParams.put("director", director);
        queryParams.put("hidden", hidden);
        queryParams.put("limit", limit);
        queryParams.put("offset", offset);
        queryParams.put("orderby", orderby);
        queryParams.put("direction", direction);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (SearchMovieRequestModel) ModelValidator.verifyModel(jsonText, SearchMovieRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Movie configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieSearch());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set http method type
        cr.setHttpMethodType(Constants.getRequest);
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

    @Path("get/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid)
    {
        ServiceLogger.LOGGER.info("Received request to search movie by id.");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieGet());
//        // set the request model
//        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set http method type
        cr.setHttpMethodType(Constants.getRequest);
        // set path param
        cr.setPathParam(movieid);

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

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovieRequest(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to add movie.");

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

        MovieAddRequestModel requestModel;

        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (MovieAddRequestModel) ModelValidator.verifyModel(jsonText, MovieAddRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Movie configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieAdd());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
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

    @Path("delete/{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid)
    {
        ServiceLogger.LOGGER.info("Received request to delete movie by id.");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieDelete());
//        // set the request model
//        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set http method type
        cr.setHttpMethodType(Constants.deleteRequest);
        // set path param
        cr.setPathParam(movieid);

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

    @Path("genre")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresRequest(@Context HttpHeaders headers)
    {
        ServiceLogger.LOGGER.info("Received request to get all genres.");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreGet());
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set http method type
        cr.setHttpMethodType(Constants.getRequest);

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

    @Path("genre/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenreRequest(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to add genre.");

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

        GenreAddRequestModel requestModel;

        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (GenreAddRequestModel) ModelValidator.verifyModel(jsonText, GenreAddRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Movie configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreAdd());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
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

    @Path("genre/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresForMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid)
    {
        ServiceLogger.LOGGER.info("Received request to get movie genres by id.");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreGet());
//        // set the request model
//        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set http method type
        cr.setHttpMethodType(Constants.getRequest);
        // set path param
        cr.setPathParam(movieid);

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

    @Path("star/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starSearchRequest(
            @Context HttpHeaders headers,
            @QueryParam("name") String name,
            @QueryParam("birthYear") int birthYear,
            @QueryParam("movieTitle") String movieTitle,
            @QueryParam("limit") int limit,
            @QueryParam("offset") int offset,
            @DefaultValue("rating") @QueryParam("orderby") String orderby,
            @DefaultValue("desc") @QueryParam("direction") String direction)
    {
        ServiceLogger.LOGGER.info("Received request to search movies.");

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

        StarSearchRequestModel requestModel;

        limit = (limit != 10 || limit != 25 || limit != 50 || limit != 100 || limit < 0) ? 10 : limit;
        offset = (offset < 0 || offset % limit != 0) ? 0 : offset;

        String n = (name == null) ? "" : "\t\"name\":\"" + name + "\",\n";
        String by = (birthYear == 0) ? "" : "\t\"birthYear\":" + birthYear + ",\n";
        String mv = (movieTitle == null) ? "" : "\t\"movieTitle\":\"" + movieTitle + "\",\n";

        String off = "\t\"offset\":" + offset + ",\n";
        String lim = "\t\"limit\":" + limit + ",\n";
        String dir = "\t\"direction\":\"" + direction + "\",\n";
        String order = "\t\"orderby\":\"" + orderby + "\"\n";
        String jsonText = "{\n" + n + by + mv + off + lim + dir + order + "}";

        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", name);
        queryParams.put("birthYear", birthYear);
        queryParams.put("movitTitle", movieTitle);
        queryParams.put("limit", limit);
        queryParams.put("offset", offset);
        queryParams.put("orderby", orderby);
        queryParams.put("direction", direction);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (StarSearchRequestModel) ModelValidator.verifyModel(jsonText, StarSearchRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Movie configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarSearch());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set http method type
        cr.setHttpMethodType(Constants.getRequest);
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

    @Path("star/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStarRequest(@Context HttpHeaders headers, @PathParam("id") String id)
    {
        ServiceLogger.LOGGER.info("Received request to search stars by id.");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarGet());
//        // set the request model
//        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set http method type
        cr.setHttpMethodType(Constants.getRequest);
        // set path param
        cr.setPathParam(id);

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

    @Path("star/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarRequest(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to add star.");

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

        StarAddRequestModel requestModel;

        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (StarAddRequestModel) ModelValidator.verifyModel(jsonText, StarAddRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Movie configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarAdd());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
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

    @Path("star/starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarToMovieRequest(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to get stars in movie.");

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

        StarsInRequestModel requestModel;

        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (StarsInRequestModel) ModelValidator.verifyModel(jsonText, StarsInRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Movie configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarIn());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
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

    @Path("rating")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRatingRequest(@Context HttpHeaders headers, String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to rate movie.");

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

        RatingRequestModel requestModel;

        ServiceLogger.LOGGER.info("JsonText: " + jsonText);

        // Map jsonText to RequestModel
        try
        {
            requestModel = (RatingRequestModel) ModelValidator.verifyModel(jsonText, RatingRequestModel.class);
        }
        catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GeneralResponseModel.class);
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Movie configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPRating());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
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

    @Path("search/{letter}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByLetter(
            @Context HttpHeaders headers,
            @PathParam("letter") String letter,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit,
            @DefaultValue("rating") @QueryParam("orderby") String orderby,
            @DefaultValue("desc") @QueryParam("direction") String direction)
    {
        ServiceLogger.LOGGER.info("Received request to search movie by id.");

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

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("offset", offset);
        queryParams.put("limit", limit);
        queryParams.put("orderby", orderby);
        queryParams.put("direction", direction);

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();
        ServiceLogger.LOGGER.info("Transaction id: " + transactionID);

        // Create ClientRequest wrapper for HTTP request.
        ClientRequest cr = new ClientRequest();
        // get the Billing URI from Movie configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from Movie configs
        cr.setEndpoint("/search/");
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set the email from headers
        cr.setEmail(headers.getHeaderString("email"));
        // set sessionID from headers
        cr.setSessionID(headers.getHeaderString("sessionID"));
        // set http method type
        cr.setHttpMethodType(Constants.getRequest);
        // set path param
        cr.setPathParam(letter);
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
}
