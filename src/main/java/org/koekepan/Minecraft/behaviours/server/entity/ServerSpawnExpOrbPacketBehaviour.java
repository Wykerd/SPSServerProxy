package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnExpOrbPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.EntityTracker;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerSpawnExpOrbPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerSpawnExpOrbPacketBehaviour() {
    }

    public ServerSpawnExpOrbPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerSpawnExpOrbPacket serverSpawnExpOrbPacket = (ServerSpawnExpOrbPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());

        double x = serverSpawnExpOrbPacket.getX();
        double y = serverSpawnExpOrbPacket.getY();
        double z = serverSpawnExpOrbPacket.getZ();

        int entityId = serverSpawnExpOrbPacket.getEntityId();
//        UUID uuid = serverSpawnExpOrbPacket.getUUID();

        if (!EmulatedClientConnection.isPlayer(entityId)){
            new EntityTracker(x, y, z, entityId, null);
        }

//        Logger.log(this, Logger.Level.DEBUG, new String[]{"Entity", "spawnEntity", "behaviour"}, "Spawn entity: " + entityId + " :: Experience (" + serverSpawnExpOrbPacket.getExp() + ")");
        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "Herobrine", (int) x, (int) z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) x, (int) z, 0, emulatedClientConnection.getUsername());
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
