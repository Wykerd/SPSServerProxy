package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPropertiesPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntityPropertiesPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
    private boolean retry = true;
//    private IServerSession serverSession;

    private ServerEntityPropertiesPacketBehaviour() {
    }

    public ServerEntityPropertiesPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityPropertiesPacket serverEntityPropertiesPacket = (ServerEntityPropertiesPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        int x = 0;
        int y = 0;
        int z = 0;

        if (EntityTracker.isEntity(serverEntityPropertiesPacket.getEntityId())) {
            x = (int) EntityTracker.getXByEntityId(serverEntityPropertiesPacket.getEntityId());
            y = (int) EntityTracker.getYByEntityId(serverEntityPropertiesPacket.getEntityId());
            z = (int) EntityTracker.getZByEntityId(serverEntityPropertiesPacket.getEntityId());
        } else if(EmulatedClientConnection.isPlayer(serverEntityPropertiesPacket.getEntityId())) {
            x = (int) EmulatedClientConnection.getXByEntityId(serverEntityPropertiesPacket.getEntityId());
            y = (int) EmulatedClientConnection.getYByEntityId(serverEntityPropertiesPacket.getEntityId());
            z = (int) EmulatedClientConnection.getZByEntityId(serverEntityPropertiesPacket.getEntityId());
        } else {
            if (retry) {
                long startTime = System.currentTimeMillis();
                while (!EntityTracker.isEntity(serverEntityPropertiesPacket.getEntityId()) && System.currentTimeMillis() - startTime < 200) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (EntityTracker.isEntity(serverEntityPropertiesPacket.getEntityId())) {
                    process(packet);
                    return;
                } else {
                    System.out.println("ServerEntityPropertiesPacketBehaviour::process => Error: Entity not found for packet: " + packet.getClass().getSimpleName() + " Entity Id: " + serverEntityPropertiesPacket.getEntityId());
                }
                emulatedClientConnection.getPacketSender().removePacket(packet);
            }
            return;
        }


        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", x, z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());
//            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
