package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntitySetPassengersPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerEntitySetPassengersPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerEntitySetPassengersPacketBehaviour() {
    }

    public ServerEntitySetPassengersPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerEntitySetPassengersPacket serverEntitySetPassengersPacket = (ServerEntitySetPassengersPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());

        int x = 0;
        int y = 0;
        int z = 0;

        if (EntityTracker.isEntity(serverEntitySetPassengersPacket.getEntityId())) {
            x = (int) EntityTracker.getXByEntityId(serverEntitySetPassengersPacket.getEntityId());
            y = (int) EntityTracker.getYByEntityId(serverEntitySetPassengersPacket.getEntityId());
            z = (int) EntityTracker.getZByEntityId(serverEntitySetPassengersPacket.getEntityId());
        } else if(EmulatedClientConnection.isPlayer(serverEntitySetPassengersPacket.getEntityId())) {
            x = (int) EmulatedClientConnection.getXByEntityId(serverEntitySetPassengersPacket.getEntityId());
            y = (int) EmulatedClientConnection.getYByEntityId(serverEntitySetPassengersPacket.getEntityId());
            z = (int) EmulatedClientConnection.getZByEntityId(serverEntitySetPassengersPacket.getEntityId());
        } else {
            System.out.println("ServerEntitySetPassengersPacketBehaviour::process => (ERROR) No entity found with Entity Id: " + serverEntitySetPassengersPacket.getEntityId());
            emulatedClientConnection.getPacketSender().removePacket(packet);
//            Logger.log(this, Logger.Level.ERROR, new String[]{"Entity", "setPassengers", "behaviour"}, "Entity not found: " + serverEntitySetPassengersPacket.getEntityId());
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
