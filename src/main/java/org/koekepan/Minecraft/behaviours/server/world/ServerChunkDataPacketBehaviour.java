package org.koekepan.Minecraft.behaviours.server.world;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Connection.PacketSender;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class ServerChunkDataPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;

//    private static HashMap<EmulatedClientConnection, Integer> migrationChunkCounters = new HashMap<>();
    private static final ConcurrentHashMap<EmulatedClientConnection, Integer> migrationChunkCounters = new ConcurrentHashMap<>();
    //    private IServerSession serverSession;

    private ServerChunkDataPacketBehaviour() {
    }

    public ServerChunkDataPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {

        System.out.println("ServerChunkDataPacketBehaviour::process => ServerChunkDataPacket received");

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


//        if (this.emulatedClientConnection.isMigratingIn()) { // Don't send chunk data to client if it is migrating in - server and player should have correct chunk data based on subscriptions
//
//            System.out.println("ServerChunkDataPacketBehaviour::process => Player \"" + emulatedClientConnection.getUsername() + "\" is migrating in. Not sending chunk data to client.");
//
//            int counter = migrationChunkCounters.getOrDefault(emulatedClientConnection, 0);
//            counter++;
//
//            if (counter == 441) { // 441 is the number of chunks in a 21x21 area, which is the area of chunks that are
//                 //sent to the client when connecting to a server. The player already has these chunks,
//                 //so we don't need to send them
//
//                System.out.println("ServerChunkDataPacketBehaviour::process => Player \"" + emulatedClientConnection.getUsername() + "\" has finished migrating in. Sending chunk packets to client.");
//                emulatedClientConnection.setMigratingIn(false);
//                migrationChunkCounters.remove(emulatedClientConnection);
//            } else {
//                migrationChunkCounters.put(emulatedClientConnection, counter);
//            }
//
////            if (packet == null) {
////                System.out.println("ServerChunkDataPacketBehaviour::process => Packet is null");
////                return;
////            }
//
//            try {
//                System.out.println("ServerChunkDataPacketBehaviour::process => Player \"" + emulatedClientConnection.getUsername() + "\" is migrated in " + counter + " of 441 chunks.");
//                emulatedClientConnection.getPacketSender().removePacket(packet);
//            } catch (Exception e) {
//                System.out.println("ServerChunkDataPacketBehaviour::process => Something is null");
////                e.printStackTrace();
////                throw new RuntimeException(e);
//            }
////            emulatedClientConnection.getPacketSender().removePacket(packet);
//        } else {
            SPSPacket spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0, 0, 0, emulatedClientConnection.getUsername()); // This is player spicific
//        SPSPacket spsPacket = new SPSPacket(packet, "clientBound", x, z, 500);
//        emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
            PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
            PacketWrapper.setProcessed(packet, true);
//        }
    }
}
