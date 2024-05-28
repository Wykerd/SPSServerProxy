package org.koekepan.VAST.Connection;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import org.koekepan.App;
import org.koekepan.VAST.Packet.PacketHandler;
import org.koekepan.VAST.Packet.PacketWrapper;

import java.util.HashMap;
import java.util.HashSet;

import static org.koekepan.App.emulatedClientInstances;


public class EmulatedClientConnection {
    private Session session;
    private Client client;

    private String username;
    private int entityID;
    private boolean connected;

    private double x_position = 0;
    private double y_position = 0;
    private double z_position = 0;

    private PacketHandler packetHandler;

    private final PacketSender packetSender = new PacketSender();
    public static HashSet<Integer> playerEntityIds = new HashSet<>(); // This is a set of entityIds that are players

    public EmulatedClientConnection(String Host, int Port, String username) {
        this.username = username;
        this.client = new Client(Host, Port, new MinecraftProtocol(username), new TcpSessionFactory());
        this.session = client.getSession();
        this.entityID = -1;
        this.connected = false;

        this.packetSender.setClientSession(this.session);

        this.session.addListener(new SessionAdapter() {
            @Override
            public void packetReceived(com.github.steveice10.packetlib.event.session.PacketReceivedEvent event) { // Receive Packet from server

                PacketWrapper packetWrapper = new PacketWrapper( (Packet) event.getPacket() );
//                packetWrapper.setPlayerSpecific(username);
                packetWrapper.clientBound = true;

                PacketWrapper.packetWrapperMap.put(event.getPacket(), packetWrapper);

//                System.out.println("<" + username + "> Received packet from server: " + event.getPacket().getClass().getSimpleName());

                packetHandler.addPacket(packetWrapper);
                packetSender.addClientBoundPacket(event.getPacket());
            }

            @Override
            public void connected(com.github.steveice10.packetlib.event.session.ConnectedEvent event) {
                System.out.println("<" + username + "> Connected to server");
            }

            @Override
            public void disconnected(com.github.steveice10.packetlib.event.session.DisconnectedEvent event) {
                System.out.println("<" + username + "> Disconnected: " + event.getReason());
            }

        });
    }

    public static boolean isPlayer(int entityId) {
        return playerEntityIds.contains(entityId);
    }

    public static EmulatedClientConnection getClientInstanceByUsername(String username) {
        for (EmulatedClientConnection emulatedClientConnection : emulatedClientInstances.values()) {
            if (emulatedClientConnection.getUsername().equals(username)) {
                return emulatedClientConnection;
            }
        }
        return null;
    }


    public void connect() {
        this.session.connect();
        this.connected = true;
    }

    public PacketSender getPacketSender() {
        return this.packetSender;
    }

    public void setPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public PacketHandler getPacketHandler() {
        return this.packetHandler;
    }

    public void disconnect() {
        this.connected = false;
        this.packetSender.stop();
        this.session.disconnect("Disconnected");

        playerEntityIds.remove(entityID);
        emulatedClientInstances.remove(session);
        App.emulatedClientInstancesByUsername.remove(username);
    }

    public boolean isConnected() {
        return this.connected;
    }

    public String getUsername() {
        return this.username;
    }

    public int getEntityID() {
        return this.entityID;
    }

    public void setEntityID(int entityID) {
        this.entityID = entityID;
        playerEntityIds.add(entityID);
    }

    public Session getSession() {
        return this.session;
    }

    public Client getClient() {
        return this.client;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void sendPacketToServer(Packet packet) {
        this.session.send(packet); // This will send the packet to the server, because the session was created, not received via a packet as in SPSClientProxy
    }

    public void movePlayer(double x, double y, double z, boolean isRelative) {
//        this.x_position = 0;
//        this.y_position = 0;

        if (isRelative) {
            this.x_position += x;
            this.y_position += y;
            this.z_position += z;
        } else {
            this.x_position = x;
            this.y_position = y;
            this.z_position = z;
        }
    }

    public double getXPosition() {
        return this.x_position;
    }
    public double getYPosition() {
        return this.y_position;
    }
    public double getZPosition() {
        return this.z_position;
    }

    public static double getXByEntityId(int entityId) {
//        public static HashMap<Session, EmulatedClientConnection> emulatedClientInstances = new HashMap<Session, EmulatedClientConnection>();
        for (EmulatedClientConnection emulatedClientConnection : emulatedClientInstances.values()) {
            if (emulatedClientConnection.getEntityID() == entityId) {
                return emulatedClientConnection.getXPosition();
            }
        }
        return 0;
    }

    public static double getYByEntityId(int entityId) {
        for (EmulatedClientConnection emulatedClientConnection : emulatedClientInstances.values()) {
            if (emulatedClientConnection.getEntityID() == entityId) {
                return emulatedClientConnection.getYPosition();
            }
        }
        return 0;
    }

    public static double getZByEntityId(int entityId) {
        for (EmulatedClientConnection emulatedClientConnection : emulatedClientInstances.values()) {
            if (emulatedClientConnection.getEntityID() == entityId) {
                return emulatedClientConnection.getZPosition();
            }
        }
        return 0;
    }


}
