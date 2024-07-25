package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.Performance.PacketCapture;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntityMovementPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerEntityMovementPacketBehaviour() {
    }

    public ServerEntityMovementPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    private boolean retry = true;
    @Override
    public void process(Packet packet) {
        ServerEntityMovementPacket serverEntityMovementPacket = (ServerEntityMovementPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        double deltaX = serverEntityMovementPacket.getMovementX();
        double deltaY = serverEntityMovementPacket.getMovementY();
        double deltaZ = serverEntityMovementPacket.getMovementZ();

        double x;
        double z;

        int entityId = serverEntityMovementPacket.getEntityId();

        if (EntityTracker.isEntity(entityId)) {
            EntityTracker.moveByEntityId(entityId, deltaX, deltaY, deltaZ);
            x = EntityTracker.getXByEntityId(entityId);
            z = EntityTracker.getZByEntityId(entityId);

        } else if (EmulatedClientConnection.isPlayer(entityId)){
//            PlayerTracker.moveByEntityId(entityId, deltaX, deltaY, deltaZ);
            x = EmulatedClientConnection.getXByEntityId(entityId);
            z = EmulatedClientConnection.getZByEntityId(entityId);

            String message = "Username <" + emulatedClientConnection.getUsername() + "> publishing move at: <" + x + ", " + z +"> for entity <" + entityId + ">";
        } else {
//            if (retry) {
//                new Thread(() -> {
//                    try {
//                        Thread.sleep(20);
//                        process(packet);
//                        retry = false;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }).start();
//            } else {
//                System.out.println("ServerEntityMovementPacketBehaviour::process => Error: Entity not found for packet: " + packet.getClass().getSimpleName() + " Entity Id: " + serverEntityMovementPacket.getEntityId());
//            }

            String message = "<ERROR!> Username <" + emulatedClientConnection.getUsername() + "> for entity <" + entityId + "> movement: <" + deltaX + ", " + deltaY + ", " + deltaZ + "> not found in EntityTracker or EmulatedClientConnection";

            return;
        }

        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());
//            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
        }

        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
