package edu.uci.ics.sctse.service.basic.configs;

import edu.uci.ics.sctse.service.basic.models.ConfigsModel;
import edu.uci.ics.sctse.service.basic.logger.ServiceLogger;

public class Configs
{
    // Default service configs
    private final String DEFAULT_SCHEME = "http://";
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int DEFAULT_PORT = 7178;
    private final String DEFAULT_PATH = "/api/basicService";
    // Default logger configs
    private final String DEFAULT_OUTPUTDIR = "./logs/";
    private final String DEFAULT_OUTPUTFILE = "basicService.log";

    // Default database driver and settings
    private final String DEFAULT_DBDRIVER = "com.mysql.cj.jdbc.Driver";
    private final String DEFAULT_DBSETTINGS = "?autoReconnect=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=PST";

    // Service configs
    private String scheme;
    private String hostName;
    private int port;
    private String path;

    // Logger configs
    private String outputDir;
    private String outputFile;

    // Database configs
    private String dbUsername;
    private String dbPassword;
    private String dbHostname;
    private int dbPort;
    private String dbDriver;
    private String dbName;
    private String dbSettings;

    private boolean isPasswordProvided = false;

    public Configs()
    {
        scheme = DEFAULT_SCHEME;
        hostName = DEFAULT_HOSTNAME;
        port = DEFAULT_PORT;
        path = DEFAULT_PATH;
        outputDir = DEFAULT_OUTPUTDIR;
        outputFile = DEFAULT_OUTPUTFILE;
    }

    public Configs(ConfigsModel cm) throws NullPointerException
    {
        if (cm == null)
        {
            throw new NullPointerException("Unable to create Configs from ConfigsModel.");
        } else
        {
            // Set service configs
            scheme = cm.getServiceConfig().get("scheme");
            if (scheme == null)
            {
                scheme = DEFAULT_SCHEME;
                System.err.println("Scheme not found in configuration file. Using default.");
            } else
            {
                System.err.println("Scheme: " + scheme);
            }

            hostName = cm.getServiceConfig().get("hostName");
            if (hostName == null)
            {
                hostName = DEFAULT_HOSTNAME;
                System.err.println("Hostname not found in configuration file. Using default.");
            } else
            {
                System.err.println("Hostname: " + hostName);
            }

            port = Integer.parseInt(cm.getServiceConfig().get("port"));
            if (port == 0)
            {
                port = DEFAULT_PORT;
                System.err.println("Port not found in configuration file. Using default.");
            } else if (port < 1024 || port > 65536)
            {
                port = DEFAULT_PORT;
                System.err.println("Port is not within valid range. Using default.");
            } else
            {
                System.err.println("Port: " + port);
            }

            path = cm.getServiceConfig().get("path");
            if (path == null)
            {
                path = DEFAULT_PATH;
                System.err.println("Path not found in configuration file. Using default.");
            } else
            {
                System.err.println("Path: " + path);
            }

            // Set logger configs
            outputDir = cm.getLoggerConfig().get("outputDir");
            if (outputDir == null)
            {
                outputDir = DEFAULT_OUTPUTDIR;
                System.err.println("Logging output directory not found in configuration file. Using default.");
            } else
            {
                System.err.println("Logging output directory: " + outputDir);
            }

            outputFile = cm.getLoggerConfig().get("outputFile");
            if (outputFile == null)
            {
                outputFile = DEFAULT_OUTPUTFILE;
                System.err.println("Logging output file not found in configuration file. Using default.");
            } else
            {
                System.err.println("Logging output file: " + outputFile);
            }

            // Set database configs
            dbUsername = cm.getDatabaseConfig().get("dbUsername");
            if (dbUsername == null)
                System.err.println("No database username found in config file.");
            else
                System.err.println("Database username: " + dbUsername);

            dbPassword = cm.getDatabaseConfig().get("dbPassword");
            if (dbPassword == null)
                System.err.println("No database password found in config file.");
            else
            {
                isPasswordProvided = true;
                System.err.println("Database password found in config file.");
            }

            dbHostname = cm.getDatabaseConfig().get("dbHostname");
            if (dbHostname == null)
                System.err.println("No database hostname found in config file.");
            else
                System.err.println("Database hostname: " + dbHostname);

            dbPort = Integer.parseInt(cm.getDatabaseConfig().get("dbPort"));
            if (dbPort == 0)
                System.err.println("No database port found in config file.");
            else if (dbPort < 1024 || dbPort > 65536)
                System.err.println("Port is not within valid range (1024-65536).");
            else
                System.err.println("Database port: " + dbPort);

            dbName = cm.getDatabaseConfig().get("dbName");
            if (dbName == null)
                System.err.println("No database name found in config file.");
            else
                System.err.println("Database name: " + dbName);

            dbDriver = cm.getDatabaseConfig().get("dbDriver");
            if (dbDriver == null)
            {
                dbDriver = DEFAULT_DBDRIVER;
                System.err.println("No database driver found in config file.");
            }
            else
                System.err.println("Database driver: " + dbDriver);

            dbSettings = cm.getDatabaseConfig().get("dbSettings");
            if (dbSettings == null)
            {
                dbSettings = DEFAULT_DBSETTINGS;
                System.err.println("No database settings found in driver");
            }
            else
                System.err.println("Database settings found in config file.");

        }
    }

    public void currentConfigs()
    {
        ServiceLogger.LOGGER.config("Scheme: " + scheme);
        ServiceLogger.LOGGER.config("Hostname: " + hostName);
        ServiceLogger.LOGGER.config("Port: " + port);
        ServiceLogger.LOGGER.config("Logger output directory: " + outputDir);
        ServiceLogger.LOGGER.config("Logger output file: " + outputFile);
        ServiceLogger.LOGGER.config("Database username: " + dbUsername);
        ServiceLogger.LOGGER.config("Database password provided?: " + isPasswordProvided);
        ServiceLogger.LOGGER.config("Database hostname: " + dbHostname);
        ServiceLogger.LOGGER.config("Database port: " + dbPort);
        ServiceLogger.LOGGER.config("Database driver: " + dbDriver);
        ServiceLogger.LOGGER.config("Database name: " + dbName);
    }

    public String getScheme()
    {
        return scheme;
    }

    public String getHostName()
    {
        return hostName;
    }

    public int getPort()
    {
        return port;
    }

    public String getPath()
    {
        return path;
    }

    public String getOutputDir()
    {
        return outputDir;
    }

    public String getOutputFile()
    {
        return outputFile;
    }

    public String getDbUsername()
    {
        return dbUsername;
    }

    public String getDbPassword()
    {
        return dbPassword;
    }

    public String getDbHostname()
    {
        return dbHostname;
    }

    public int getDbPort()
    {
        return dbPort;
    }

    public String getDbName()
    {
        return dbName;
    }

    public String getDbDriver()
    {
        return dbDriver;
    }

    public String getDbSettings()
    {
        return dbSettings;
    }
}
