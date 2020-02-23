package wk1.activity2.hello_world_rest_call.resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

// Set the Path for this resource
@Path("test")
public class TestPage {
    // Set the path for this function
    @Path("hello")

    // Set the HTTP request method
    @GET

    // Set the type of content that will be sent in response
    @Produces(MediaType.APPLICATION_JSON)

    public Response helloWorld() {
        System.err.println("Hello world!");
        return Response.status(Status.OK).build();
    }

    // Set the path for this function
    @Path("product")

    // Set the HTTP request method
    @POST
    @Consumes(MediaType.APPLICATION_JSON)

    // Set the type of content that will be sent in response
    @Produces(MediaType.APPLICATION_JSON)

    public Response getProduct(String jsonText) {
        System.err.println("Getting product...");

        // Create an ObjectMapper to parse the JSON text.
        ObjectMapper mapper = new ObjectMapper();

        int num1;
        int num2;
        int product;
        String resultJSON = "";

        try {
            // Get the first integer from the JSON text
            num1 = mapper.readTree(jsonText).get("num1").asInt();

            // Get the second integer from the JSON text
            num2 = mapper.readTree(jsonText).get("num2").asInt();

            product = num1 * num2;
            System.err.println(num1 + " * " + num2 + " = " + product);

            // Create a string to return as JSON
            resultJSON = Integer.toString(product);
        } catch (IOException e) {
            // Some error occurred extracting values from the JSON text
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        // Success. Return a JSON string in the response
        return Response.status(Status.OK).entity(resultJSON).build();
    }
}
