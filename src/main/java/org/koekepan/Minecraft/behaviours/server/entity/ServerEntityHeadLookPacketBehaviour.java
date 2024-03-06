package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntityHeadLookPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerEntityHeadLookPacketBehaviour() {
    }

    public ServerEntityHeadLookPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityHeadLookPacket serverEntityHeadLookPacket = (ServerEntityHeadLookPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        int entityId = serverEntityHeadLookPacket.getEntityId();
        double x;
        double z;

        if (EmulatedClientConnection.isPlayer(entityId)){
            x = EmulatedClientConnection.getXByEntityId(entityId);
            z = EmulatedClientConnection.getZByEntityId(entityId);
        } else if (EntityTracker.isEntity(entityId)){
            x = EntityTracker.getXByEntityId(entityId);
            z = EntityTracker.getZByEntityId(entityId);
        } else {
//            throw new RuntimeException("No entity found with Entity Id: " + entityId);
//            Logger.log(this, Logger.Level.ERROR, new String[]{"behaviour", "entityMovement"}, "No entity found with Entity Id: " + entityId);
            return;
        }

        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) x, (int) z, 0, emulatedClientConnection.getUsername());
        }
//        SPSPacket spsPacket = new SPSPacket(packet, proxySession.getUsername(), (int) x, (int) z, 0);
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
