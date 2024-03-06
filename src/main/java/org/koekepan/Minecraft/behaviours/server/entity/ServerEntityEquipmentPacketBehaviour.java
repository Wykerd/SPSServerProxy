package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntityEquipmentPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerEntityEquipmentPacketBehaviour() {
    }

    public ServerEntityEquipmentPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityEquipmentPacket serverEntityEquipmentPacket = (ServerEntityEquipmentPacket) packet;

        int x = 0;
        int y = 0;
        int z = 0;

        if (EmulatedClientConnection.isPlayer(serverEntityEquipmentPacket.getEntityId())) {
            x = (int) EmulatedClientConnection.getXByEntityId(serverEntityEquipmentPacket.getEntityId());
            y = (int) EmulatedClientConnection.getYByEntityId(serverEntityEquipmentPacket.getEntityId());
            z = (int) EmulatedClientConnection.getZByEntityId(serverEntityEquipmentPacket.getEntityId());
        } else if (EntityTracker.isEntity(serverEntityEquipmentPacket.getEntityId())){
            x = (int) EntityTracker.getXByEntityId(serverEntityEquipmentPacket.getEntityId());
            y = (int) EntityTracker.getYByEntityId(serverEntityEquipmentPacket.getEntityId());
            z = (int) EntityTracker.getZByEntityId(serverEntityEquipmentPacket.getEntityId());
        } else {
            System.out.println("ServerEntityEquipmentPacketBehaviour::process => (ERROR) No entity found with Entity Id: " + serverEntityEquipmentPacket.getEntityId());
            emulatedClientConnection.getPacketSender().removePacket(packet);
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
