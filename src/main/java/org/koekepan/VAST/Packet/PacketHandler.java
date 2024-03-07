package org.koekepan.VAST.Packet;

import com.github.steveice10.packetlib.packet.Packet;
//import org.koekepan.VAST.Connection.ClientConnectedInstance;
import org.koekepan.Minecraft.behaviours.ClientBoundPacketBehaviours;
import org.koekepan.Minecraft.behaviours.ServerBoundPacketBehaviours;
import org.koekepan.VAST.Connection.EmulatedClientConnection;

import java.util.ArrayList;
import java.util.List;

public class PacketHandler {

//    Deque<PacketWrapper> packetQueue = new ArrayDeque<PacketWrapper>();
    BehaviourHandler<Packet> behaviourHandler;
    List<Thread> threads = new ArrayList<>(); // List to store all threads


    public PacketHandler(EmulatedClientConnection clientInstance) {
        this.behaviourHandler = new BehaviourHandler<Packet>();
        ClientBoundPacketBehaviours clientBoundPacketBehaviours = new ClientBoundPacketBehaviours(clientInstance);
        clientBoundPacketBehaviours.registerForwardingBehaviour();

        ServerBoundPacketBehaviours serverBoundPacketBehaviours = new ServerBoundPacketBehaviours(clientInstance);
        serverBoundPacketBehaviours.registerForwardingBehaviour();

        //Merge the behaviours
        BehaviourHandler<Packet> behaviourHandler = BehaviourHandler.mergeBehaviourHandlers(clientBoundPacketBehaviours, serverBoundPacketBehaviours);

        this.setBehaviours(behaviourHandler);
    }

    public void addPacket(PacketWrapper packetWrapper) {
        if (packetWrapper != null) {
            if (!packetWrapper.isProcessed) {
                Packet packet = packetWrapper.getPacket();
                if (packet != null) {
                    Thread thread = new Thread(() -> {
                        this.behaviourHandler.process(packet);
                    });
                    thread.start();
                    threads.add(thread); // Add the thread to the list
                } else {
                    System.out.println("PacketHandler::addPacket => Packet is null");
                }
            } else {
                System.out.println("PacketHandler::addPacket => Packet is already processed");
            }
        } else {
            System.out.println("PacketHandler::addPacket => PacketWrapper is null");
        }
    }

    public void setBehaviours(BehaviourHandler<Packet> behaviourHandler) {
        this.behaviourHandler = behaviourHandler;
    }

    public void stop() {
        // Stop all threads
        for (Thread thread : threads) {
            thread.interrupt();
        }
        threads.clear(); // Clear the list
    }
}
