package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntityVelocityPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerEntityVelocityPacketBehaviour() {
    }

    public ServerEntityVelocityPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityVelocityPacket serverEntityVelocityPacket = (ServerEntityVelocityPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        int entityId = serverEntityVelocityPacket.getEntityId();
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

            // TODO: To fix this, make it player specific, I.e. post this packet at the player position
            // TODO: This happens when player throws item - is quite possibly the reason for the metadata issue aswell!
            // TODO: Check how thrown items or items on the ground is supposed to be spawned, I think it is spawned with a metadata data packet, but the
            // issue with the metadata packet is that the entity does not yet exist.

            System.out.println("ServerEntityVelocityPacketBehaviour::process => (ERROR) No entity found with Entity Id: " + entityId);
            emulatedClientConnection.getPacketSender().removePacket(packet);

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
