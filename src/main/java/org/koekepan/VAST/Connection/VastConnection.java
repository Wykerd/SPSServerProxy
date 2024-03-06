package org.koekepan.VAST.Connection;

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
import org.koekepan.VAST.CustomPackets.EstablishConnectionPacket;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import static java.lang.Thread.sleep;
import static org.koekepan.VAST.Packet.PacketUtil.*;
import static org.koekepan.VAST.Packet.PacketWrapper.packetWrapperMap;

public class VastConnection {

    private final int VAST_COM_PORT;
    private final String VAST_COM_IP;
    private final UUID uuid = UUID.randomUUID();

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
        socket.emit("spawn_VASTclient", "Minecraft Client 1", "10.42.0.1", "20000", "100", "100");

        try {
            sleep(100);
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

                System.out.println("Received packet <"+packet.packet.getClass().getSimpleName()+"> on channel <"+packet.channel+">");

                String username = packet.username.split("&")[0];
                String unique_id = packet.username.split("&")[1];

                PacketWrapper packetWrapper = new PacketWrapper(packet.packet);
                packetWrapper.unique_id = unique_id;
                packetWrapper.clientBound = false;
                packetWrapperMap.put(packet.packet, packetWrapper);

                if (packet.channel.equals("serverBound")) {

                    if (packet.packet instanceof EstablishConnectionPacket){
                        // Create an EmulatedClientConnection to Server

                        EstablishConnectionPacket establishConnectionPacket = (EstablishConnectionPacket) packet.packet;

                        if (establishConnectionPacket.establishConnection()) {
                            App.connectNewEmulatedClient(establishConnectionPacket.getUsername());
                        } else {
//                            App.disconnectEmulatedClient(establishConnectionPacket.getUsername());
                        }

                    }
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
        socket.emit("publish", connectionID, packet.username + "&", // + PacketSender.get_UniqueId(packet.packet),
                x, y, radius, json, packet.channel); // TODO: Check the packet.username and if it is necessary
    }


    public void publishMove(int x, int y) {
        int x_position = x;
        int z_position = y;
        socket.emit("move", x_position, z_position);
    }
}
