package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnGlobalEntityPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.util.UUID;

public class ServerSpawnGlobalEntityPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerSpawnGlobalEntityPacketBehaviour() {
    }

    public ServerSpawnGlobalEntityPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerSpawnGlobalEntityPacket serverSpawnGlobalEntityPacket = (ServerSpawnGlobalEntityPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());

        double x = serverSpawnGlobalEntityPacket.getX();
        double y = serverSpawnGlobalEntityPacket.getY();
        double z = serverSpawnGlobalEntityPacket.getZ();

        int entityId = serverSpawnGlobalEntityPacket.getEntityId();
        UUID uuid = null;

        if (!EmulatedClientConnection.isPlayer(entityId)){
            new EntityTracker(x, y, z, entityId, uuid);
        }

//        Logger.log(this, Logger.Level.INFO, new String[]{"Entity", "spawnEntity", "behaviour"}, "Spawn entity: " + entityId + " :: " + serverSpawnGlobalEntityPacket.getType().toString());
        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 512, "clientBound"); // TODO: Radius is and should not be hardcoded to 512
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) x, (int) z, 0, emulatedClientConnection.getUsername());
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
