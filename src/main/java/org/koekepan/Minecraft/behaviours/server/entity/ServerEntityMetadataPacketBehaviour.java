package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.util.Arrays;

public class ServerEntityMetadataPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
    private boolean retry = true;
//    private IServerSession serverSession;

    private ServerEntityMetadataPacketBehaviour() {
    }

    public ServerEntityMetadataPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityMetadataPacket serverEntityMetadataPacket = (ServerEntityMetadataPacket) packet;

//        System.out.println("sending metadata Packet");

        int x = 0;
        int y = 0;
        int z = 0;

        synchronized (EntityTracker.class) {
            if (EntityTracker.isEntity(serverEntityMetadataPacket.getEntityId())) {
                x = (int) EntityTracker.getXByEntityId(serverEntityMetadataPacket.getEntityId());
                y = (int) EntityTracker.getYByEntityId(serverEntityMetadataPacket.getEntityId());
                z = (int) EntityTracker.getZByEntityId(serverEntityMetadataPacket.getEntityId());
            } else if (EmulatedClientConnection.isPlayer(serverEntityMetadataPacket.getEntityId())) {
                x = (int) EmulatedClientConnection.getXByEntityId(serverEntityMetadataPacket.getEntityId());
                y = (int) EmulatedClientConnection.getYByEntityId(serverEntityMetadataPacket.getEntityId());
                z = (int) EmulatedClientConnection.getZByEntityId(serverEntityMetadataPacket.getEntityId());
            } else {
                if (retry) {
                    long startTime = System.currentTimeMillis();
                    while (!EntityTracker.isEntity(serverEntityMetadataPacket.getEntityId()) && System.currentTimeMillis() - startTime < 2000) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (EntityTracker.isEntity(serverEntityMetadataPacket.getEntityId())) {
                        process(packet);
                        return;
                    } else {
                        System.out.println("ServerEntityMetadataPacketBehaviour::process => Error: Entity not found for packet: " + packet.getClass().getSimpleName() + " Entity Id: " + serverEntityMetadataPacket.getEntityId());
                    }
                    emulatedClientConnection.getPacketSender().removePacket(packet);
                }
                return;
            }


            if (!retry) {
                System.out.println("Found entity after delay, entityID: " + serverEntityMetadataPacket.getEntityId() + " in EntityTracker");
            }

            SPSPacket spsPacket;
            if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
                spsPacket = new SPSPacket(packet, "clientBound", x, z, 0, "clientBound");
            } else {
//                spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());
                spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
            }

            PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
            PacketWrapper.setProcessed(packet, true);
        }
    }
}
