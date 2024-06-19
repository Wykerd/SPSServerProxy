package org.koekepan;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private String host;
    private int port;

    // This is the VAST_COM ip/port that the proxy will connect to (aka the sps client)
    private String vastHost;
    private int vastPort;

    private String GateWayMatcherHost;
    private int GateWayMatcherPort;
    int ServerClientXPosition;
    int ServerClientYPosition;


    public AppConfig() {
        loadProperties();
    }


    public void loadProperties() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("./config.properties")) {
            // Load the properties file
            prop.load(input);

            // Get the property values
            host = prop.getProperty("MinecraftAddress");
            port = Integer.parseInt(prop.getProperty("MinecraftPort"));
            vastHost = prop.getProperty("VastComHost");
            vastPort = Integer.parseInt(prop.getProperty("VastComPort"));

            GateWayMatcherHost = prop.getProperty("GateWayMatcherHost");
            GateWayMatcherPort = Integer.parseInt(prop.getProperty("GateWayMatcherPort"));
            ServerClientXPosition = Integer.parseInt(prop.getProperty("ServerVastClientXPosition"));
            ServerClientYPosition = Integer.parseInt(prop.getProperty("ServerVastClientYPosition"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Getters for the properties
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getVastHost() {
        return vastHost;
    }

    public int getVastPort() {
        return vastPort;
    }

    public String getGateWayMatcherHost() {
        return GateWayMatcherHost;
    }

    public int getGateWayMatcherPort() {
        return GateWayMatcherPort;
    }

    public int getServerClientXPosition() {
        return ServerClientXPosition;
    }

    public int getServerClientYPosition() {
        return ServerClientYPosition;
    }

    // Main method for testing
    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        config.loadProperties();

//        System.out.println("Database URL: " + config.getDatabaseUrl());
//        System.out.println("Database User: " + config.getDatabaseUser());
//        System.out.println("Database Password: " + config.getDatabasePassword());
    }
}
