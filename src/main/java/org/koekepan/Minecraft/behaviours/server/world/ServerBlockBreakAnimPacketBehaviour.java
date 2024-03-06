package org.koekepan.Minecraft.behaviours.server.world;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockBreakAnimPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerBlockBreakAnimPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerBlockBreakAnimPacketBehaviour() {
    }

    public ServerBlockBreakAnimPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerBlockBreakAnimPacket serverBlockBreakAnimPacket = (ServerBlockBreakAnimPacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        int x = (int) serverBlockBreakAnimPacket.getPosition().getX();
        int y = (int) serverBlockBreakAnimPacket.getPosition().getY();
        int z = (int) serverBlockBreakAnimPacket.getPosition().getZ();

        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", x, z, 0, "clientBound");
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
