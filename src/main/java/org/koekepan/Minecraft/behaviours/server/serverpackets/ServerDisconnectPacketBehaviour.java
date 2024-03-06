package org.koekepan.Minecraft.behaviours.server.serverpackets;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerDisconnectPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerDisconnectPacketBehaviour() {
    }

    public ServerDisconnectPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerDisconnectPacket serverDisconnectPacket = (ServerDisconnectPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
//        int x = (int) serverDisconnectPacket.getPosition().getX();
//        int y = (int) serverDisconnectPacket.getPosition().getY();
//        int z = (int) serverDisconnectPacket.getPosition().getZ();

        double x = emulatedClientConnection.getXPosition();
        double z = emulatedClientConnection.getZPosition();

        SPSPacket spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int)x, (int)z, 0, emulatedClientConnection.getUsername());
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
