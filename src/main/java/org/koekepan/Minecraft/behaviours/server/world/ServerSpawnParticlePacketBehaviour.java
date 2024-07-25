package org.koekepan.Minecraft.behaviours.server.world;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnParticlePacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerSpawnParticlePacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;
//    private IServerSession serverSession;

    private ServerSpawnParticlePacketBehaviour() {
    }

    public ServerSpawnParticlePacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
        ServerSpawnParticlePacket serverSpawnParticlePacket = (ServerSpawnParticlePacket) packet;

//        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());        
        int x = (int) serverSpawnParticlePacket.getX();
        int y = (int) serverSpawnParticlePacket.getY();
        int z = (int) serverSpawnParticlePacket.getZ();

        SPSPacket spsPacket;
        if (emulatedClientConnection.getUsername().equals("ProxyListener2")) {
            spsPacket = new SPSPacket(packet, "clientBound", x, z, 0, "clientBound");
        } else {
            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername()  );
//            spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
        }
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
        PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
        PacketWrapper.setProcessed(packet, true);
    }
}
