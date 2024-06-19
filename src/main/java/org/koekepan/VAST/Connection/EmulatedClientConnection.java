package org.koekepan.VAST.Connection;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.koekepan.App;
import org.koekepan.VAST.Packet.PacketHandler;
import org.koekepan.VAST.Packet.PacketWrapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

import static org.koekepan.App.emulatedClientInstances;
import static org.koekepan.VAST.Packet.PacketWrapper.packetWrapperMap;


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
    private boolean MigratingIn = false;

    private int tempCounter = 0;

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

//                if (this.is)

//                EmulatedClientConnection emulatedClientConnection = getClientInstanceByUsername(username);
//                if (emulatedClientConnection != null) {
//                    if (emulatedClientConnection.isMigratingIn()
//                            && !(event.getPacket() instanceof ServerPluginMessagePacket)
//                            && !(event.getPacket() instanceof ServerJoinGamePacket)
//                            && !(event.getPacket() instanceof ServerPlayerPositionRotationPacket)
//                    ) {
//                        System.out.println("Migrating: <" + emulatedClientConnection.isMigratingIn() + "> username: " + username);
//                        if (event.getPacket() instanceof ServerJoinGamePacket) {
//                            emulatedClientConnection.setMigratingIn(false); //Migrated
//                        }
//                        if (event.getPacket() instanceof ServerPlayerPositionRotationPacket) {
//                            System.out.println("<" + username + "> Received player position rotation packet from server");
//
//                            ServerPlayerPositionRotationPacket p = event.getPacket();
//                            ClientPlayerPositionRotationPacket responsePacket = new ClientPlayerPositionRotationPacket(
//                                    true,
//                                    p.getX(),
//                                    p.getY(),
//                                    p.getZ(),
//                                    p.getYaw(),
//                                    p.getPitch()
//                            );
//
//                            // Send reponse packet to server
//
//                            PacketWrapper packetWrapper = new PacketWrapper(responsePacket);
//                            emulatedClientConnection.getPacketSender().sendPacketToServerImmediate(packetWrapper);
////                            emulatedClientConnection.setMigratingIn(false); //Migrated
//                            return;
//                        }
//
//                        return;
//                    } else {
//                        System.out.println("MIGRATING!!!! <" + username + "> Received packet from server: " + event.getPacket().getClass().getSimpleName());
//                    }
//                } else {
//                    System.out.println("EmulatedClientConnection::packetReceived => emulatedClientConnection is null for username: " + username);
//                }

//                if (event.getPacket() instanceof ServerChunkDataPacket) {
//                    System.out.println("<" + username + "> Received Chunk! packet from server");
//                }

                if (event.getPacket() instanceof ServerChunkDataPacket && isMigratingIn()) {
                    tempCounter++;

                    if (tempCounter == 441) {
                        System.out.println("Migrating: <" + isMigratingIn() + "> username: " + username);
                        setMigratingIn(false); //Migrated
                        tempCounter = 0;
                    }
                    return;
                }

                PacketWrapper packetWrapper = new PacketWrapper( (Packet) event.getPacket() );
//                packetWrapper.setPlayerSpecific(username);
                packetWrapper.clientBound = true;

                PacketWrapper.packetWrapperMap.put(event.getPacket(), packetWrapper);

                if (event.getPacket().getClass().getSimpleName().equals("ServerPluginMessagePacket")) {
                    System.out.println("<" + username + "> Received plugin message packet from server: " + event.getPacket().getClass().getSimpleName());
//                    return;
                }
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


    public void addChannelRegistration(String channel) {
        byte[] payload = writeStringToPluginMessageData(channel);
        String registerMessage = "REGISTER";
        ClientPluginMessagePacket registerPacket = new ClientPluginMessagePacket(registerMessage, payload);


        PacketWrapper packetWrapper = new PacketWrapper(registerPacket);
//        packetWrapper.unique_id = unique_id;
        packetWrapper.clientBound = false;
//        packetWrapperMap.put(registerPacket, packetWrapper);
        this.packetSender.sendPacketToServerImmediate(packetWrapper);
    }

    public void removeChannelRegistration(String channel) {
        byte[] payload = writeStringToPluginMessageData(channel);
        String unregisterMessage = "UNREGISTER";
        ClientPluginMessagePacket unregisterPacket = new ClientPluginMessagePacket(unregisterMessage, payload);
        PacketWrapper packetWrapper = new PacketWrapper(unregisterPacket);
        packetWrapper.clientBound = false;
        this.packetSender.sendPacketToServerImmediate(packetWrapper);
    }

    private byte[] writeStringToPluginMessageData(String message) {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        ByteBuf buff = Unpooled.buffer();
        buff.writeBytes(data);
        return buff.array();
    }

    public void setMigratingIn(boolean status) {
        this.MigratingIn = status;
    }

    public boolean isMigratingIn() {
        return this.MigratingIn;
    }
}
