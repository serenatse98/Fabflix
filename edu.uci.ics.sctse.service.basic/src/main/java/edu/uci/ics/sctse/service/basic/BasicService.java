package edu.uci.ics.sctse.service.basic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import edu.uci.ics.sctse.service.basic.configs.Configs;
import edu.uci.ics.sctse.service.basic.logger.ServiceLogger;
import edu.uci.ics.sctse.service.basic.models.ConfigsModel;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BasicService {
    public static BasicService basicService;

    private static Configs configs = new Configs();
    private static Connection con;

//    private String username = "cs122b_";
//    private String password = "cs122btest";

    public static void main(String[] args) {
        basicService = new BasicService();
        basicService.initService(args);
    }

    private void initService(String[] args) {
        // Validate arguments
        basicService.validateArguments(args);
        // Exec the arguments
        basicService.execArguments(args);
        // Initialize logging
        initLogging();
        ServiceLogger.LOGGER.config("Starting service...");
        configs.currentConfigs();
        // Connect to database
        connectToDatabase();
        // Initialize HTTP server
        initHTTPServer();
        ServiceLogger.LOGGER.config("Service initialized.");
    }

    private void validateArguments(String[] args) {
        boolean isConfigOptionSet = false;
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "--default":
                case "-d":
                    if (i + 1 < args.length) {
                        exitAppFailureArgs("Invalid arg after " + args[i] + " option: " + args[i + 1]);
                    }
                case "--config":
                case "-c":
                    if (!isConfigOptionSet) {
                        isConfigOptionSet = true;
                        ++i;
                    } else {
                        exitAppFailureArgs("Conflicting configuration file arguments.");
                    }
                    break;

                default:
                    exitAppFailureArgs("Unrecognized argument: " + args[i]);
            }
        }
    }

    private void execArguments(String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                switch (args[i]) {
                    case "--config":
                    case "-c":
                        // Config file specified. Load it.
                        getConfigFile(args[i + 1]);
                        ++i;
                        break;
                    case "--default":
                    case "-d":
                        System.err.println("Default config options selected.");
                        configs = new Configs();
                        break;
                    default:
                        exitAppFailure("Unrecognized argument: " + args[i]);
                }
            }
        } else {
            System.err.println("No config file specified. Using default values.");
            configs = new Configs();
        }
    }

    private void getConfigFile(String configFile) {
        try {
            System.err.println("Config file name: " + configFile);
            configs = new Configs(loadConfigs(configFile));
            System.err.println("Configuration file successfully loaded.");
        } catch (NullPointerException e) {
            System.err.println("Config file not found. Using default values.");
            configs = new Configs();
        }
    }

    private ConfigsModel loadConfigs(String file) {
        System.err.println("Loading configuration file...");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ConfigsModel configs = null;

        try {
            configs = mapper.readValue(new File(file), ConfigsModel.class);
        } catch (IOException e) {
            exitAppFailure("Unable to load configuration file.");
        }
        return configs;
    }

    private void initLogging() {
        try {
            ServiceLogger.initLogger(configs.getOutputDir(), configs.getOutputFile());
        } catch (IOException e) {
            exitAppFailure("Unable to initialize logging.");
        }
    }

    private void connectToDatabase() {
        ServiceLogger.LOGGER.config("Connecting to database...");
        // NOTE: NEVER HARD-CODE YOUR CONNECTION DETAILS
        //       USE A CONFIGURATION FILE FOR YOUR HOMEWORK!!
        //       WE ARE HARD-CODING VALUES HERE TO KEEP THE EXAMPLE SIMPLE

        String driver = configs.getDbDriver();
        String hostName = configs.getDbHostname();
        int port = configs.getDbPort();
        String dbName = configs.getDbName();
        String settings = configs.getDbSettings();
        String username = configs.getDbUsername();
        String password = configs.getDbPassword();


        String url = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName + settings;

        try {
            Class.forName(driver);
            ServiceLogger.LOGGER.config("Database URL: " + url);
            // STUDENTS USE THIS ONE
            con = DriverManager.getConnection(url, username, password);
            ServiceLogger.LOGGER.config("Connected to database: " + con.toString());
        } catch (Exception e) {
            // Listing the exception types invidually allows you to use different handlers if you choose to
            e.printStackTrace();
            if (e instanceof ClassCastException) {
                ServiceLogger.LOGGER.warning("ClassCastException");
            }
            if (e instanceof SQLException) {
                ServiceLogger.LOGGER.warning("SQLException");
            }
            if (e instanceof NullPointerException) {
                ServiceLogger.LOGGER.warning("NullPointerException");
            }
        }
    }

    private void initHTTPServer() {
        ServiceLogger.LOGGER.config("Initializing HTTP server...");
        String scheme = configs.getScheme();
        String hostName = configs.getHostName();
        int port = configs.getPort();
        String path = configs.getPath();

        try {
            URI uri = UriBuilder.fromUri(scheme + hostName + path).port(port).build();
            ResourceConfig rc = new ResourceConfig().packages("edu.uci.ics.sctse.service.basic.resources");
            rc.register(JacksonFeature.class);
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, rc, false);
            server.start();
            ServiceLogger.LOGGER.config("HTTP server started.");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void exitAppFailure(String message) {
        System.err.println("ERROR: " + message);
        System.exit(-1);
    }

    private void exitAppFailureArgs(String message) {
        System.err.println("ERROR: " + message);
        System.err.println("Usage options: ");
        System.err.println("\tSpecify configuration file:");
        System.err.println("\t\t--config [file]");
        System.err.println("\t\t-c");
        System.err.println("\tUse default configuration:");
        System.err.println("\t\t--default");
        System.err.println("\t\t-d");
        System.exit(-1);
    }

    public static Connection getCon() {
        return con;
    }
}
