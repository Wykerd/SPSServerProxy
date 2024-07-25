package org.koekepan.Minecraft.behaviours.server.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerPlayerPositionRotationPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerPlayerPositionRotationPacketBehaviour() {
    }

    public ServerPlayerPositionRotationPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerPlayerPositionRotationPacket serverPlayerPositionRotationPacket = (ServerPlayerPositionRotationPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        double x = serverPlayerPositionRotationPacket.getX();
        double y = serverPlayerPositionRotationPacket.getY();
        double z = serverPlayerPositionRotationPacket.getZ();

        String username = emulatedClientConnection.getUsername();

        emulatedClientConnection.movePlayer(x, y, z, false);

//        emulatedClientConnection.movePlayer(x, y, z, false);

        SPSPacket spsPacket = new SPSPacket(packet, username, (int) 0, (int) 0, 0, username); // Player specific
//        SPSPacket spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
