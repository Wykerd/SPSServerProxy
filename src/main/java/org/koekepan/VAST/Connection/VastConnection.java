package org.koekepan.VAST.Connection;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.packet.Packet;
import com.google.gson.Gson;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.koekepan.App;
//import org.koekepan.Minecraft.ChunkPosition;
//import org.koekepan.Performance.PacketCapture;
import org.koekepan.Minecraft.ChunkPosition;
import org.koekepan.Performance.PacketCapture;
import org.koekepan.VAST.CustomPackets.EstablishConnectionPacket;
import org.koekepan.VAST.CustomPackets.PINGPONG;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import static java.lang.Thread.sleep;
import static org.koekepan.App.config;
import static org.koekepan.VAST.Packet.PacketUtil.*;
import static org.koekepan.VAST.Packet.PacketWrapper.packetWrapperMap;

public class VastConnection {

    private final int VAST_COM_PORT;
    private final String VAST_COM_IP;
    private final UUID uuid = UUID.randomUUID();

    private int VAST_CLIENT_X_Position;
    private int VAST_CLIENT_Y_Position;

    private Socket socket;
//    private ClientConnectedInstance clientInstance;

    private int connectionID;

    public VastConnection(String VAST_COM_IP, int VAST_COM_PORT) {
        this.VAST_COM_IP = VAST_COM_IP;
        this.VAST_COM_PORT = VAST_COM_PORT;
//        this.clientInstance = clientInstance; // TODO: Probs ServerSession
    }

    public void disconnect() {
        socket.emit("disconnect_client", connectionID);
//        socket.emit("disconnect", connectionID);
        socket.disconnect();
    }

    private boolean initialiseConnection() {
        final CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        final boolean[] result = {false};
        try {
            socket = IO.socket("http://" + VAST_COM_IP + ":" + VAST_COM_PORT);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    socket.emit("handshake", uuid.toString() ,"Hello, server. This is Java Client");
                }
            }).on("handshake", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    if (("Hello, client with UUID: " + uuid.toString() + ". This is Node.js Server.").equals(args[0])) {
                        System.out.println("VAST: Successfully connected to the correct server.");
                        result[0] = true;
//						return true;
                    } else {
                        System.out.println("VAST: Failed to connect to the correct server.");
                    }
                    completableFuture.complete(true);
                }
            });
            socket.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            result[0] = completableFuture.get();  // this will block until the CompletableFuture is complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return result[0];

    }
    public boolean connect() {
        if (initialiseConnection()) {
            initialiseVASTclient();
            initialiseListeners();
            return true;
        }
        return false;
    }

    private void initialiseVASTclient() { // TODO: This subscribe should be more client-specific: (This is just for the login procedure)

        String gatewayMatcherHost = config.getGateWayMatcherHost();
        int gatewayMatcherPort = config.getGateWayMatcherPort();

        this.VAST_CLIENT_X_Position = config.getServerClientXPosition();
        this.VAST_CLIENT_Y_Position = config.getServerClientYPosition();
        socket.emit("spawn_VASTclient", "SC 1", gatewayMatcherHost, Integer.toString(gatewayMatcherPort), this.VAST_CLIENT_X_Position, VAST_CLIENT_Y_Position);

        try {
            sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.subscribe(250, 250, 1000, "serverBound");

        System.out.println("VAST Client initialised");
    }

    public void initialiseListeners() {
//		ConsoleIO.println("Initialize SPS listeners");
        socket.on("ID", new Emitter.Listener() {
            @Override
            public void call(Object... data) {
                System.out.println("Received connection ID: <" + data[0] + ">");
                connectionID = (int) data[0];
            }
        });

        socket.on("getType", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.emit("type", "server");
            }
        });

        socket.on("publication", new Emitter.Listener() {
            @Override
            public void call(Object... data) {
                final SPSPacket packet = receivePublication(data);

//                System.out.println("Received packet <"+packet.packet.getClass().getSimpleName()+"> on channel <"+packet.channel+">");

                String username = packet.username.split("&")[0];
                String unique_id = packet.username.split("&")[1];


                //////// PINGPONG PACKET HANDLING ////////
                // IF PING PACKET we need to create a clientbound PONG packet, with FULLPONG
                if (packet.packet instanceof PINGPONG) {
                    PINGPONG pingpong = (PINGPONG) packet.packet;
                    if (pingpong.getType() == PINGPONG.Type.PING) {

//                        PINGPONG pong = new PINGPONG(PINGPONG.Direction.CLIENTBOUND, PINGPONG.Origin.ServerProxy, PINGPONG.Type.PONG);
//                        pong.setPingOriginServerID(pingpong.getPingOriginServerID()); // Very Important
//                        pong.setInitTime(pingpong.getInitTime()); // Very Important

//                        PacketWrapper packetWrapper = new PacketWrapper(pong);
//                        packetWrapper.unique_id = unique_id;
//                        packetWrapper.clientBound = true;
//                        packetWrapperMap.put(pong, packetWrapper);

                        // LOG
                        PacketCapture.log(username, "PING_" + unique_id, PacketCapture.LogCategory.SERVERBOUND_PING_IN);


                        // PONGING!
//                        System.out.println("VastConnection.java => (INFO) Received PING packet, sending PONG packet");
//                        App.emulatedClientInstancesByUsername.get(username).getPacketSender().addClientBoundPacket(pong);
//                        PacketWrapper.set_unique_id(pong, unique_id);
//                        App.emulatedClientInstancesByUsername.get(username).getPacketHandler().addPacket(packetWrapper); // TODO: Will need to add behaviour for PINGPONG packet
//                        return;
                    }
                }
                //////////////////////////////////////////



                PacketWrapper packetWrapper = new PacketWrapper(packet.packet);
                packetWrapper.unique_id = unique_id;
                packetWrapper.clientBound = false;
                packetWrapperMap.put(packet.packet, packetWrapper);

                PacketCapture.log(username,packet.packet.getClass().getSimpleName() + "_" + unique_id, PacketCapture.LogCategory.SERVERBOUND_IN);

                if (packet.channel.equals("serverBound")) {

                    if (packet.packet instanceof EstablishConnectionPacket){
                        // Create an EmulatedClientConnection to Server

                        EstablishConnectionPacket establishConnectionPacket = (EstablishConnectionPacket) packet.packet;
                        username = establishConnectionPacket.getUsername();

//                        System.out.println("VastConnection.java => (INFO) Received establish connection packet with details: "+establishConnectionPacket.toString());

                        if (establishConnectionPacket.establishConnection()) {

                            if (establishConnectionPacket.getIp() != null) {
                                if (Objects.equals(establishConnectionPacket.getIp(), App.getMinecraftHost()) && establishConnectionPacket.getPort() == App.getMinecraftPort()) {
                                    System.out.println("VastConnection.java => (INFO) Received establish connection packet from VAST_COM with correct IP and Port");

                                    if (!App.emulatedClientInstancesByUsername.containsKey(username)) {
                                        App.connectNewEmulatedClient(establishConnectionPacket.getUsername());
                                        App.emulatedClientInstancesByUsername.get(username).setMigratingIn(true);
                                    } else {
                                        App.emulatedClientInstancesByUsername.get(username).disconnect();
                                        App.connectNewEmulatedClient(establishConnectionPacket.getUsername());
                                        App.emulatedClientInstancesByUsername.get(username).setMigratingIn(true);
                                    }

                                } else {
                                    System.out.println("VastConnection.java => (ERROR) Received establish connection packet from VAST_COM with incorrect IP and Port");
//                                    System.out.println("VastConnection.java => (ERROR) Expected IP: "+App.getMinecraftHost()+" Port: "+App.getMinecraftPort());
//                                    System.out.println("VastConnection.java => (ERROR) Received IP: "+establishConnectionPacket.getIp()+" Port: "+establishConnectionPacket.getPort());
                                    return;
                                }
                            } else {
                                System.out.println("VastConnection.java => (INFO) Received establish connection packet from VAST_COM");
                                App.connectNewEmulatedClient(establishConnectionPacket.getUsername());
                            }
//                            packetWrapperMap.remove(packet.packet);
//                            return;
                        } else {
                            System.out.println("received disconnect packet");

                            if (establishConnectionPacket.getIp() != null){
                                if (Objects.equals(establishConnectionPacket.getIp(), App.getMinecraftHost()) && establishConnectionPacket.getPort() == App.getMinecraftPort()) {
                                    System.out.println("VastConnection.java => (INFO) Disconnected User: <" + establishConnectionPacket.getUsername() + "> from VAST_COM with correct IP and Port");
                                    if (App.emulatedClientInstancesByUsername.containsKey(username)) {
                                        App.emulatedClientInstancesByUsername.get(username).disconnect();
                                    } else {
                                        System.out.println("VastConnection.java => (ERROR) Received disconnect packet from VAST_COM with correct IP and Port but user not found in emulatedClientInstancesByUsername");
                                        System.out.println("VastConnection.java => (ERROR) The users that are connected are: "+App.emulatedClientInstancesByUsername.keySet());
                                        System.out.println("VastConnection.java => (ERROR) The user that disconnected is: <"+username+ ">");
//                                        System.out.println("VastConnection.java => (ERROR) The if statement is : "+App.emulatedClientInstancesByUsername.containsKey(username));
                                    }
//                                    App.emulatedClientInstancesByUsername.get(username).disconnect();
                                    return;
                                } else {
                                    System.out.println("VastConnection.java => (ERROR) Received disconnect connection packet from VAST_COM with incorrect IP and Port");
                                    System.out.println("VastConnection.java => (ERROR) Expected IP: "+App.getMinecraftHost()+" Port: "+App.getMinecraftPort());
                                    System.out.println("VastConnection.java => (ERROR) Received IP: "+establishConnectionPacket.getIp()+" Port: "+establishConnectionPacket.getPort());
                                }
                            } else {
                                System.out.println("VastConnection.java => (INFO) Disconnected User: " + establishConnectionPacket.getUsername() + " from VAST_COM");
                                App.emulatedClientInstancesByUsername.get(username).disconnect();
                                return;
                            }
//                            packetWrapperMap.remove(packet.packet);
//                            return;
                        }

                    }

                    // Can get connected client based on username of packet, I don't like this, I want to use session if possible
                    if (App.emulatedClientInstancesByUsername.containsKey(username)) {
                        App.emulatedClientInstancesByUsername.get(username).getPacketSender().addServerBoundPacket(packet.packet);
                        App.emulatedClientInstancesByUsername.get(username).getPacketHandler().addPacket(packetWrapper);
                    } else {
                        System.out.println("VastConnection.java => (ERROR) Received packet <"+packet.packet.getClass().getSimpleName()+"> on channel <"+packet.channel+">");
                    }
//                    packetWrapperMap.remove(packet.packet);
                    // add to serverBoundQueue
                    // in the add, also add to packethandler
                } else {
//                    App.clientInstances.get(packet.username).sendPacketToClient(packet.packet);
                    System.out.println("VastConnection.java => (ERROR) Received packet <"+packet.packet.getClass().getSimpleName()+"> on channel <"+packet.channel+">");
                }

            }
        });
    }

    public void subscribe(int x, int z, int aoi) {
        subscribe(x, z, aoi, null);
    }

    public void subscribe(int x, int z, int aoi, String channel) {
        socket.emit("subscribe", x, z, aoi, channel == null ? "serverBound" : channel);
    }

    public void subscribePolygon(List<ChunkPosition> positions){
        List<float[]> posList = new ArrayList<float[]>();
        for (ChunkPosition position : positions) {
            posList.add(new float[]{position.getX(), position.getZ()});
        }

        String jsonPositions = new Gson().toJson(posList);

//		ConsoleIO.println("Length of jsonpositions: " + jsonPositions.toString());

//        socket.emit("clearsubscriptions", "serverBound");
        socket.emit("subscribe_polygon", jsonPositions, "serverBound");

//        if (!listeners.isEmpty()) {
//            String username = listeners.values().iterator().next().getUsername();
//            socket.emit("clearsubscriptions", username);
//            socket.emit("subscribe_polygon", jsonPositions, username);
//        }

    }

    public void unsubscribe(String channel) {
        socket.emit("clearsubscriptions", channel);
    }

    public void subscribeMobile(int x, int y, int aoi, String channel) {
        socket.emit("subscribe_mobile", x, y, aoi, channel);
    }

    public void subscribeMobilePolygon(List<ChunkPosition> positions, String channel) {
        List<float[]> posList = new ArrayList<float[]>();
        for (ChunkPosition position : positions) {
            posList.add(new float[]{position.getX(), position.getZ()});
        }

        String jsonPositions = new Gson().toJson(posList);

        socket.emit("subscribe_mobile_polygon", jsonPositions, channel);
    }

    public void publish(SPSPacket packet) { // sends to vast matcher as client

//        if (packet.packet.getClass().getSimpleName().equals("ServerEntityDestroyPacket")) {
//            System.out.println("VastConnection.java => (INFO) Received ServerEntityDestroyPacket");
//            return;
//        }

//        System.out.println("Connection <"+uuid+"> sent packet <"+packet.packet.getClass().getSimpleName()+"> on channel <"+packet.channel+">");

        //convert to JSON
        Gson gson = new Gson();
        byte[] payload = packetToBytes(packet.packet);
        String json = gson.toJson(payload);
        //ConsoleIO.println("Connection <"+connectionID+"> sent packet <"+packet.packet.getClass().getSimpleName()+"> on channel <"+packet.channel+">");

        int x = packet.x;
        int y = packet.y;
        int radius = packet.radius;

//        temp_pubcounter += 1;
//        Logger.log(this, Logger.Level.DEBUG, new String[]{"counter", "clientPub"},"Amount of packets sent: " + temp_pubcounter + ": " + packet.packet.getClass().getSimpleName());
//        PacketCapture.log(packet.packet.getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(packet.packet), PacketCapture.LogCategory.SERVERBOUND_OUT);

        if (packet.packet instanceof PINGPONG) {
//            if (((PINGPONG) packet.packet).getType() == PINGPONG.Type.PING) {
//                PacketCapture.log(packet.packet.getClass().getSimpleName() + "_" + PacketWrapper.getPacketWrapper(packet.packet).unique_id, PacketCapture.LogCategory.PING_OUT);
//                socket.emit("publish", connectionID,
//                        packet.username + "&" + PacketWrapper.getPacketWrapper(packet.packet).unique_id + "&PING",
//                        x, y, radius, json, packet.channel);
//                return;
//            }
            if (((PINGPONG) packet.packet).getType() == PINGPONG.Type.PONG) {
                PacketCapture.log("PONG_" + PacketWrapper.getPacketWrapper(packet.packet).unique_id, PacketCapture.LogCategory.CLIENTBOUND_PONG_OUT);
                socket.emit("publish", connectionID,
                        packet.username + "&" + PacketWrapper.getPacketWrapper(packet.packet).unique_id + "&PROXYPONG",
                        x, y, radius, json, packet.channel);
                return;
            } else {
                // Print and error, this should not happen
                System.out.println("VastConnection.java => (ERROR) PINGPONG packet type is not PING or PONG");
            }
        }


        PacketCapture.log(packet.packet.getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(packet.packet), PacketCapture.LogCategory.CLIENTBOUND_OUT);

        socket.emit("publish", connectionID, packet.username + "&" + PacketWrapper.getPacketWrapper(packet.packet).unique_id, // + PacketSender.get_UniqueId(packet.packet),
                x, y, radius, json, packet.channel); // TODO: Check the packet.username and if it is necessary
    }


    public void publishMove(int x, int y) {
        int x_position = x;
        int z_position = y;
        socket.emit("move", x_position, z_position);
    }

    public int getVAST_CLIENT_X_Position() {
        return VAST_CLIENT_X_Position;
    }

    public int getVAST_CLIENT_Y_Position() {
        return VAST_CLIENT_Y_Position;
    }
}
