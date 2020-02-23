package edu.uci.ics.sctse.service.basic.resources;

import edu.uci.ics.sctse.service.basic.core.SentenceRecords;
import edu.uci.ics.sctse.service.basic.logger.ServiceLogger;
import edu.uci.ics.sctse.service.basic.models.GetNumSentencesResponseModel;
import edu.uci.ics.sctse.service.basic.models.GetSentenceFromIDResponseModel;
import edu.uci.ics.sctse.service.basic.models.GetSentenceResponseModel;
import edu.uci.ics.sctse.service.basic.models.SentenceModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("get")
public class GetRecords
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumRequest()
    {
        ServiceLogger.LOGGER.info("Received request for number of records.");
        GetSentenceResponseModel responseModel;

        ServiceLogger.LOGGER.info("Retrieving sentences...");
        responseModel = SentenceRecords.getAllSentencesFromDB();
        ServiceLogger.LOGGER.info("Sentences retrieved.");


        if (responseModel.isValid())
        {
            int x = responseModel.getNumberOfSentences();
            ServiceLogger.LOGGER.info("Creating new num response model.");
            GetNumSentencesResponseModel numResponseModel;
            numResponseModel = new GetNumSentencesResponseModel(
                    0, "Number of records successfully retrieved.", x);
            ServiceLogger.LOGGER.info("ResponseModel data is valid.");

            return Response.status(Status.OK).entity(numResponseModel).build();
        }

        else
        {
            ServiceLogger.LOGGER.info("ResponseModel data is invalid.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
        }
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSentenceFromID(@PathParam("id") int id)
    {
        ServiceLogger.LOGGER.info("Received request for sentence with id#" + id);
        GetSentenceResponseModel responseModel;

        ServiceLogger.LOGGER.info("Retrieving sentences...");
        responseModel = SentenceRecords.getSentenceFromID(id);
        ServiceLogger.LOGGER.info("Sentences retrieved.");

        if (responseModel.isValid())
        {
            if (responseModel.getNumberOfSentences() > 0)
            {
                ServiceLogger.LOGGER.info("Retrieving single sentence...");
                SentenceModel sm = responseModel.getSingleSentence();
                GetSentenceFromIDResponseModel sentenceResponseModel = new GetSentenceFromIDResponseModel(
                        0, "Record successfully retrieved", sm);
                ServiceLogger.LOGGER.info("Got sentence.");

                return Response.status(Status.OK).entity(sentenceResponseModel).build();
            }

            else if (responseModel.getNumberOfSentences() == 0)
            {
                ServiceLogger.LOGGER.info("No sentence found at id#" + id);
                GetSentenceFromIDResponseModel noSentenceRModel = new GetSentenceFromIDResponseModel(
                        1, "Record not found.", "");
                return Response.status(Status.OK).entity(noSentenceRModel).build();
            }
        }
        ServiceLogger.LOGGER.info("ResponseModel data is invalid.");
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(responseModel).build();
    }

}
