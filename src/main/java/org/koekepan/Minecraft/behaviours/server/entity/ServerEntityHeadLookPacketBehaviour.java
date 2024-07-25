package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.Performance.PacketCapture;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.util.Map;

public class ServerEntityHeadLookPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerEntityHeadLookPacketBehaviour() {
    }

    public ServerEntityHeadLookPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    private boolean retry = true; // TODO: Change back to true by default

//    @Override
    private void _process(Packet packet) {
        ServerEntityHeadLookPacket serverEntityHeadLookPacket = (ServerEntityHeadLookPacket) packet;

//        PacketWrapper packetWrapper = PacketWrapper.getPacketWrapper(packet);

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


            if (retry) {
                new Thread(() -> {
                    try {

                        Thread.sleep(10);
                        process(packet, false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                return;
            } else {
                System.out.println("ServerEntityHeadLookPacketBehaviour::process => Error: Entity not found for packet: " + packet.getClass().getSimpleName() + " Entity Id: " + serverEntityHeadLookPacket.getEntityId());
//                Remove packet from queue

//                StringBuilder message = new StringBuilder(packetWrapper.unique_id + "_Entity not found; Entity Id: " + serverEntityHeadLookPacket.getEntityId());
//                message.append("; Entities that are tracked: ");
//    // Use entrySet to iterate over HashMap
//                for (Map.Entry<Integer, EntityTracker> entry : EntityTracker.getEntities().entrySet()) {
//                    message.append(entry.getValue().getEntityId()).append(", ");
//                }
//
//    // Optionally, remove the last comma and space for better formatting
//                if (message.charAt(message.length() - 2) == ',') {
//                    message.setLength(message.length() - 2);
//                }
//
//                PacketCapture.log(
//                        this.emulatedClientConnection.getUsername(),
//                        message.toString(),
//                        PacketCapture.LogCategory.CLIENTBOUND_ENTITY_BEH
//                );


            emulatedClientConnection.getPacketSender().removePacket(packet);
            }
            return;
        }


        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {

            spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0, "clientBound");
        } else {

            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());
//            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
        }
//        SPSPacket spsPacket = new SPSPacket(packet, proxySession.getUsername(), (int) x, (int) z, 0);
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);


        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }

    public void process(Packet packet, Boolean retry) {
        this.retry = retry; // Almost always false, should basically never be true
        _process(packet);
    }

    @Override
    public void process(Packet packet) {
        process(packet, true);
    }
}
