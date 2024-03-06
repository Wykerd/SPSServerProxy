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

        if (EntityTracker.isEntity(serverEntityMetadataPacket.getEntityId())) {
            x = (int) EntityTracker.getXByEntityId(serverEntityMetadataPacket.getEntityId());
            y = (int) EntityTracker.getYByEntityId(serverEntityMetadataPacket.getEntityId());
            z = (int) EntityTracker.getZByEntityId(serverEntityMetadataPacket.getEntityId());
        } else if(EmulatedClientConnection.isPlayer(serverEntityMetadataPacket.getEntityId())) {
            x = (int) EmulatedClientConnection.getXByEntityId(serverEntityMetadataPacket.getEntityId());
            y = (int) EmulatedClientConnection.getYByEntityId(serverEntityMetadataPacket.getEntityId());
            z = (int) EmulatedClientConnection.getZByEntityId(serverEntityMetadataPacket.getEntityId());
        } else {
//            PacketSender.removePacketFromQueue(packet);
            System.out.println("ServerEntityMetadataPacketBehaviour::process => (ERROR) No entity found with Entity Id: " + serverEntityMetadataPacket.getEntityId());

            SPSPacket spsPacket;
            if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
                spsPacket = new SPSPacket(packet, "clientBound", 100, 100, 10000, "clientBound");
            } else {
                spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) 100, (int) 100, 10000, emulatedClientConnection.getUsername());
            }
            PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
            PacketWrapper.setProcessed(packet, true);
            return;
        }


        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", x, z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) x, (int) z, 0, emulatedClientConnection.getUsername());
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
