package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityCollectItemPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntityCollectItemPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
    private boolean retry = true;
//    private IServerSession serverSession;

    private ServerEntityCollectItemPacketBehaviour() {
    }

    public ServerEntityCollectItemPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityCollectItemPacket serverEntityCollectItemPacket = (ServerEntityCollectItemPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        int x = 0;
        int y = 0;
        int z = 0;

        if (EmulatedClientConnection.isPlayer(serverEntityCollectItemPacket.getCollectorEntityId())) {
            x = (int) EmulatedClientConnection.getXByEntityId(serverEntityCollectItemPacket.getCollectorEntityId());
            y = (int) EmulatedClientConnection.getYByEntityId(serverEntityCollectItemPacket.getCollectorEntityId());
            z = (int) EmulatedClientConnection.getZByEntityId(serverEntityCollectItemPacket.getCollectorEntityId());
        } else if (EntityTracker.isEntity(serverEntityCollectItemPacket.getCollectorEntityId())){
            x = (int) EntityTracker.getXByEntityId(serverEntityCollectItemPacket.getCollectorEntityId());
            y = (int) EntityTracker.getYByEntityId(serverEntityCollectItemPacket.getCollectorEntityId());
            z = (int) EntityTracker.getZByEntityId(serverEntityCollectItemPacket.getCollectorEntityId());
        } else {
            if (retry) {
                new Thread(() -> {
                    try {
                        Thread.sleep(20);
                        process(packet);
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                System.out.println("ServerEntityCollectItemPacketBehaviour::process => Error: Entity not found for packet: " + packet.getClass().getSimpleName() + " Entity Id: " + serverEntityCollectItemPacket.getCollectorEntityId());
            }
            return;
        }

        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", x, z, 0, "clientBound");
        } else {
//            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
