package wk1.activity2.hello_world_rest_call;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;

public class BasicService {
    public static void main(String[] args) {
        // Set the scheme
        String scheme = "http://";
        // Let Grizzly HTTP server listen on any host address.
        String hostName = "0.0.0.0";
        // Set the path to listen for incoming REST calls on
        String path = "/api/basicService";
        // Set the port to listen on. Can be any arbitrary port for this example.
        int listenPort = 6761;

        // Initiate the embedded Grizzly server
        try {
            /* Build the URI. A URI is a string of characters used to identify a
                resource on a network.
            */
            URI uri = UriBuilder.fromUri(scheme + hostName + path).port(listenPort).build();


            /* Tell Jersey which resources it will manage. These must be packages
                containing the java classes Jersey will call upon when a REST call
                is received by Grizzly.
             */
            ResourceConfig rc = new ResourceConfig().packages("wk1.activity2.hello_world_rest_call");


            /* Set Jackson to be the serializer for Jersey. This allows us to easily convert
                JSON strings to POJOs, and POJOs to JSON strings. It will also allow us to parse
                configuration files, which we will cover later.
             */
            rc.register(JacksonFeature.class);


            /* Create the server. You must specify the URI the server will listen on and
                the ResourceConfig we created.
             */
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, rc, false);


            // Start the server
            server.start();

        } catch (IOException e) {
            System.err.println("Unable to start server.");
            throw new RuntimeException();
        }
    }
}