package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntityStatusPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
    private boolean retry = true;
//    private IServerSession serverSession;

    private ServerEntityStatusPacketBehaviour() {
    }

    public ServerEntityStatusPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntityStatusPacket serverEntityStatusPacket = (ServerEntityStatusPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());


        int entityId = serverEntityStatusPacket.getEntityId();
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
                        Thread.sleep(20);
                        process(packet);
                        retry = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                System.out.println("ServerEntityStatusPacketBehaviour::process => Error: Entity not found for packet: " + packet.getClass().getSimpleName() + " Entity Id: " + serverEntityStatusPacket.getEntityId());
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
}
