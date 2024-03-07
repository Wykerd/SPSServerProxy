package org.koekepan.VAST.Connection;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
//import org.koekepan.Performance.PacketCapture;
import org.koekepan.VAST.Connection.PacketSenderRunnables.ClientSender;
import org.koekepan.VAST.Connection.PacketSenderRunnables.ServerSender;
import org.koekepan.VAST.Packet.PacketWrapper;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.koekepan.App;

//import static org.koekepan.VAST.Connection.ClientConnectedInstance.clientInstances_PacketSenders;
import static org.koekepan.VAST.Packet.PacketWrapper.packetWrapperMap;

public class PacketSender { // This is the packet sender, it sends packets to the VAST_COM server and the Client (Clientbound and Serverbound)
    public final ConcurrentHashMap<Integer, PacketWrapper> clientboundPacketQueue = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, PacketWrapper> serverboundPacketQueue = new ConcurrentHashMap<>();

//    public final ConcurrentHashMap
//    private int queueNumberClientbound = 0;    // The queue number of the next packet to be sent to the client
    public int queueNumberServerbound = 0;    // The queue number of the next packet to be sent to the server
    public int queueNumberClientboundLast = 0;    // The queue number of the last packet in the queue
    public int queueNumberServerboundLast = 0;    // The queue number of the last packet in the queue
    private Session clientSession;

    private ClientSender serverSender;
    private ServerSender clientSender;

    public PacketSender() {
    }

    public void start() {
        startClientSender();
        startServerSender();
    }

    private ScheduledExecutorService packetExecutor;
    private ScheduledExecutorService packetExecutor2;

    public void startServerSender() {
        this.serverSender = new ClientSender(this, clientSession ); // See note at startClientSender
        packetExecutor = Executors.newSingleThreadScheduledExecutor();
        packetExecutor.scheduleAtFixedRate(serverSender, 0, 1, TimeUnit.MILLISECONDS);
    }

    public void startClientSender() {
//        this.clientSender = new ClientSender(this, this.clientSession);
        this.clientSender = new ServerSender(this, App.getVastConnection()); // New ServerSender because it is copied from clientProrxy and send to a session
        packetExecutor2 = Executors.newSingleThreadScheduledExecutor();
        packetExecutor2.scheduleAtFixedRate(clientSender, 0, 1, TimeUnit.MILLISECONDS);
    }

    public void stopServerSender() {
        if (packetExecutor != null) {
            packetExecutor.shutdown();
            packetExecutor = null;
        }
    }

    public void stopClientSender() {
        if (packetExecutor2 != null) {
            packetExecutor2.shutdown();
            packetExecutor2 = null;
        }
    }

    public void addServerBoundPacket(Packet packet) {
        PacketWrapper packetWrapper = PacketWrapper.getPacketWrapper(packet);
        packetWrapper.clientBound = false;
//        synchronized (PacketSender.class) { // Synchronize on the class object if static fields are being modified
        queueNumberServerboundLast++;
        packetWrapper.queueNumber = queueNumberServerboundLast;
        serverboundPacketQueue.put(queueNumberServerboundLast, packetWrapper);
//        }
        // ConsoleIO.println("PacketSender.addClientboundPacket: " + packet.getClass().getSimpleName());
    }

    public void addClientBoundPacket(Packet packet) {
        PacketWrapper packetWrapper = PacketWrapper.getPacketWrapper(packet);
        packetWrapper.clientBound = true;
        packetWrapper.unique_id = "CB" + UUID.randomUUID().toString().substring(0, 4) + (queueNumberClientboundLast+1);
//        packetWrapperMap.put(packet, packetWrapper);

//        clientInstances_PacketSenders.get(this).getPacketHandler().addPacket(packetWrapper);

//        System.out.println("PacketSender.addServerboundPacket: " + packet.getClass().getSimpleName());
        packetWrapper.queueNumber = ++queueNumberClientboundLast;
        packetWrapper.unique_id = "SB" + UUID.randomUUID().toString().substring(0, 4) + queueNumberClientboundLast;
        clientboundPacketQueue.put(queueNumberClientboundLast, packetWrapper);
//        PacketCapture.log(packet.getClass().getSimpleName() + "_" + packetWrapper.unique_id, PacketCapture.LogCategory.SERVERBOUND_IN);
    }

    public void setClientSession(Session session) {
        this.clientSession = session;
    }

//    public static boolean isPacketInClientboundQueue(Packet packet) {
//        return clientboundPacketQueue.containsKey(PacketWrapper.get_QueueNumber(packet));
//    }

    public void removePacket(Packet packet) {
        PacketWrapper packetWrapper = packetWrapperMap.get(packet);
        if (packetWrapper != null) {
            if (packetWrapper.clientBound) {
//                synchronized (clientboundPacketQueue) {
                    clientboundPacketQueue.remove(packetWrapper.queueNumber);
//                }
            } else {
//                synchronized (serverboundPacketQueue) {
                    serverboundPacketQueue.remove(packetWrapper.queueNumber);
//                }
            }
        }

//        PacketCapture.log(packet.getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(packet), PacketCapture.LogCategory.DELETED_PACKETS);
        PacketWrapper.removePacketWrapper(packet);
    }

    public void stop() {
        stopServerSender();
        stopClientSender();
        clientboundPacketQueue.clear();
        serverboundPacketQueue.clear();
//        packetWrapperMap.clear();
    }
}
