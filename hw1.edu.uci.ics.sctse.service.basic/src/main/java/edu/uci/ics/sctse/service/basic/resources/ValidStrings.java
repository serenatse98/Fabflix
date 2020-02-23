package edu.uci.ics.sctse.service.basic.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.sctse.service.basic.core.SentenceRecords;
import edu.uci.ics.sctse.service.basic.logger.ServiceLogger;
import edu.uci.ics.sctse.service.basic.models.InsertSentenceRequestModel;
import edu.uci.ics.sctse.service.basic.models.ValidStringResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("validateString")
public class ValidStrings
{
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateString(String jsonText)
    {
        ServiceLogger.LOGGER.info("Received request to validate sentence.");
        ServiceLogger.LOGGER.info(jsonText);
        ObjectMapper mapper = new ObjectMapper();
        InsertSentenceRequestModel requestModel;

        try
        {
            ServiceLogger.LOGGER.info("Mapping JSON to request model");
            requestModel = mapper.readValue(jsonText, InsertSentenceRequestModel.class);

            if (isValidLength(requestModel.getSentence(), requestModel.getLen()) &&
                requestModel.getSentence().length() < 512 && requestModel.getSentence() != "" &&
                requestModel.getLen() > 0)
            {
                ServiceLogger.LOGGER.info("sentence length matched inputted len");
                ValidStringResponseModel responseModel = new ValidStringResponseModel(
                        0, "String length matched.");

                ServiceLogger.LOGGER.info("Inputting into database...");
                SentenceRecords.insertSentenceIntoDB(requestModel);
                ServiceLogger.LOGGER.info("Sentence entered into database.");

                return Response.status(Status.OK).entity(responseModel).build();
            }

            else if (!isValidLength(requestModel.getSentence(), requestModel.getLen())&&
                    requestModel.getSentence().length() < 512 && requestModel.getSentence() != "" &&
                    requestModel.getLen() > 0)
            {
                ServiceLogger.LOGGER.info("Sentence length does not match inputted len.");
                ValidStringResponseModel responseModel = new ValidStringResponseModel(
                        1, "String length does not match.");
                ServiceLogger.LOGGER.info("Sentence was NOT entered into database.");

                return Response.status(Status.OK).entity(responseModel).build();
            }

            else if (requestModel.getSentence().equals(""))
            {
                ServiceLogger.LOGGER.info("Sentence is empty.");
                ValidStringResponseModel responseModel = new ValidStringResponseModel(
                        3, "Input string is empty.");
                ServiceLogger.LOGGER.info("Sentence was NOT entered into database.");

                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }

            else if (requestModel.getSentence().length() > 512)
            {
                ServiceLogger.LOGGER.info("Input string is over 512 chars.");
                ValidStringResponseModel responseModel = new ValidStringResponseModel(
                        4, "Input string is too long.");
                ServiceLogger.LOGGER.info("Sentence was NOT entered into database.");

                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }

            else if (requestModel.getLen() < 0)
            {
                ServiceLogger.LOGGER.info("Entered length is negative.");
                ValidStringResponseModel responseModel = new ValidStringResponseModel(
                        5, "Length value is negative.");
                ServiceLogger.LOGGER.info("Sentence was NOT entered into database.");

                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }

            else
            {
                ServiceLogger.LOGGER.info("Returning an empty response body.");
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        catch (IOException e)
        {
            if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("Unable to map JSON to POJO.");
                ServiceLogger.LOGGER.info("JSON not in correct format..");
                ValidStringResponseModel responseModel = new ValidStringResponseModel(
                        2, "Invalid request format.");
                ServiceLogger.LOGGER.info("Sentence was NOT entered into database.");

                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("Unable to parse JSON.");
                ServiceLogger.LOGGER.info("JSON not in correct format..");
                ValidStringResponseModel responseModel = new ValidStringResponseModel(
                        2, "Invalid request format.");
                ServiceLogger.LOGGER.info("Sentence was NOT entered into database.");

                return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
            }
            else {
                ServiceLogger.LOGGER.warning("IOException.");
                e.printStackTrace();
            }
        }


        return Response.status(Status.NOT_IMPLEMENTED).build();
    }

    private boolean isValidLength(String input, int len)
    {
        ServiceLogger.LOGGER.info("Validate length == number of words in sentence");
        String [] splitInput = input.split("[ -]");
        return len == splitInput.length;
    }
}
