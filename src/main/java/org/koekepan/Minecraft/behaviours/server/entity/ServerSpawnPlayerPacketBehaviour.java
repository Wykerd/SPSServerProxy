package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.util.Arrays;
import java.util.UUID;

public class ServerSpawnPlayerPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerSpawnPlayerPacketBehaviour() {
    }

    public ServerSpawnPlayerPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerSpawnPlayerPacket serverSpawnPlayerPacket = (ServerSpawnPlayerPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        double x = serverSpawnPlayerPacket.getX();
        double y = serverSpawnPlayerPacket.getY();
        double z = serverSpawnPlayerPacket.getZ();

        int entityId = serverSpawnPlayerPacket.getEntityId();
        UUID uuid = serverSpawnPlayerPacket.getUUID();

        if (!(EmulatedClientConnection.isPlayer(entityId))){
//            PlayerTracker.setEntityId(entityId, uuid);
//            if (EntityTracker.getEntityTrackerByEntityId(entityId) == null) {
//                new EntityTracker(x, y, z, entityId, uuid);
//            }
        }

//        Logger.log(this, Logger.Level.INFO, new String[]{"Entity", "spawnPlayer", "behaviour"}, "Spawn entity: " + entityId + " :: " + Arrays.toString(serverSpawnPlayerPacket.getMetadata()));

        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", (int) x, (int) z, 0, "clientBound");
        } else {
//            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());
//            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) x, (int) z, 0, "clientBound");
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
