package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.util.UUID;

public class ServerSpawnPaintingPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerSpawnPaintingPacketBehaviour() {
    }

    public ServerSpawnPaintingPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerSpawnPaintingPacket serverSpawnPaintingPacket = (ServerSpawnPaintingPacket) packet;
//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());

        double x = serverSpawnPaintingPacket.getPosition().getX();
        double y = serverSpawnPaintingPacket.getPosition().getY();
        double z = serverSpawnPaintingPacket.getPosition().getZ();

        int entityId = serverSpawnPaintingPacket.getEntityId();
        UUID uuid = serverSpawnPaintingPacket.getUUID();

        if (!EmulatedClientConnection.isPlayer(entityId)){
            new EntityTracker(x, y, z, entityId, uuid);
        }

//        Logger.log(this, Logger.Level.DEBUG, new String[]{"Entity", "spawnEntity", "behaviour"}, "Spawn entity: " + entityId + " :: Painting (" + serverSpawnPaintingPacket.getPaintingType().name() + ")");
        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) x, (int) z, 0, emulatedClientConnection.getUsername());
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
