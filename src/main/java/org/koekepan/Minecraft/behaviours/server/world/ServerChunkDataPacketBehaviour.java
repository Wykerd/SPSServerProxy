package org.koekepan.Minecraft.behaviours.server.world;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Connection.PacketSender;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import static java.lang.Thread.sleep;

public class ServerChunkDataPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerChunkDataPacketBehaviour() {
    }

    public ServerChunkDataPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerChunkDataPacket serverChunkDataPacket = (ServerChunkDataPacket) packet;

//        if (PacketSender.get_UniqueId(packet) == null) { // If this pause is not here, the packet will be processed before the unique id is set and likely be lost.
//            try {
//                sleep(100);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
//        int x = (int) serverChunkDataPacket.getColumn().getX()* 16;
////        int y = (int) serverChunkDataPacket.getColumn().getY();
//        int z = (int) serverChunkDataPacket.getColumn().getZ()* 16;

        int x = (int) emulatedClientConnection.getXPosition();
        int z = (int) emulatedClientConnection.getZPosition();

//        SPSPacket spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), x, z, 0, emulatedClientConnection.getUsername());
        SPSPacket spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername()); // This is player spicific
//        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", x, z, 500);
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
