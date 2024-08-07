package org.koekepan;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Connection.PacketSender;
import org.koekepan.VAST.Connection.VastConnection;
import org.koekepan.VAST.Packet.PacketHandler;
import org.koekepan.VAST.Packet.PacketWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class App
{
    public static AppConfig config = new AppConfig();
//    config.loadProperties();

    // This is the server ip/port that the proxy will listen on (aka the server that is emulated)
    static String minecraftHost = config.getHost();
    static int minecraftPort = config.getPort();

    // This is the VAST_COM ip/port that the proxy will connect to (aka the sps client)
    static String vastHost = config.getVastHost();
    static int vastPort = config.getVastPort();

    private static VastConnection vastConnection;
    public static HashMap<Session, EmulatedClientConnection> emulatedClientInstances = new HashMap<Session, EmulatedClientConnection>();
    public static HashMap<String, EmulatedClientConnection> emulatedClientInstancesByUsername = new HashMap<String, EmulatedClientConnection>();

    public static EmulatedClientConnection proxyPlayerClient;

//    private static final PacketHandler packetHandler = new PacketHandler();
//    public static PacketSender packetSender = new PacketSender();

    public App() {
        // 0. Initialize the packet sender
//        PacketSender packetSender = new PacketSender();


        // 2. Create VAST_COM connection
        // For each client that connects to the server, create a emulatedClientConnection and add to HASHMAP clientInstances

        String command = "vast_com";
//        vastPort = vastPort;
        String argument = Integer.toString(vastPort);
        ProcessBuilder processBuilder = new ProcessBuilder(command, argument, " &"); //> /dev/null 2>&1
        // processBuilder.directory(new File("/path/to/working/directory"));
        // Start the process in the background
        try {
            Process process = processBuilder.start();
            System.out.println("VAST_com started in background with port: " + vastPort);
            sleep(300); // Sleep for 300ms to allow VAST_COM to start en connect to matcher
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        vastConnection = new VastConnection(vastHost, vastPort);
        vastConnection.connect();

        connectProxyPlayer();

    }

    public static void main(String[] args )
    {

        System.out.println( "Hello World! We are starting the SPSServerProxy!");
        System.out.println("VAST_COM Host: " + vastHost + " Port: " + vastPort);
        System.out.println("Minecraft Server Host: " + minecraftHost + " Port: " + minecraftPort);

        new App();
    }

    public static VastConnection getVastConnection() {
        return vastConnection;
    }


    private static void connectProxyPlayer() {
        // 1. Create a Fake Player and connect to server
        MinecraftProtocol protocol = new MinecraftProtocol("ProxyListener2");
        EmulatedClientConnection emulatedClientConnection = new EmulatedClientConnection(minecraftHost, minecraftPort, "ProxyListener2");
        emulatedClientConnection.setPacketHandler(new PacketHandler(emulatedClientConnection));

        emulatedClientConnection.connect();
        emulatedClientConnection.getPacketSender().startClientSender();
        emulatedClientConnection.getPacketSender().setServerSenderUsername("ProxyListener2");

        proxyPlayerClient = emulatedClientConnection;

//        emulatedClientConnection.addChannelRegistration("Koekepan|partition");

//         Wait 5 seonds then Every second in new thread register for partition
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            emulatedClientConnection.addChannelRegistration("Koekepan|partition");
            emulatedClientConnection.removeChannelRegistration("Koekepan|partition");
        }, 2, 5, TimeUnit.SECONDS);
    }

    public static void connectNewEmulatedClient(String username) {
        EmulatedClientConnection emulatedClientConnection = new EmulatedClientConnection(minecraftHost, minecraftPort, username);
        emulatedClientConnection.setPacketHandler(new PacketHandler(emulatedClientConnection));

        emulatedClientInstances.put(emulatedClientConnection.getSession(), emulatedClientConnection);
        emulatedClientInstancesByUsername.put(username, emulatedClientConnection);

        emulatedClientConnection.connect();

        emulatedClientConnection.getPacketSender().startClientSender();
        emulatedClientConnection.getPacketSender().setServerSenderUsername(username);

        emulatedClientConnection.getPacketSender().startServerSender();
    }

    public static String getMinecraftHost() {
        return minecraftHost;
    }

    public static int getMinecraftPort() {
        return minecraftPort;
    }

}
