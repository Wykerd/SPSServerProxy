//package org.koekepan.herobrineproxy.packet.behaviours.server.world;
//
//import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;
//import com.github.steveice10.packetlib.packet.Packet;
//import org.koekepan.herobrineproxy.behaviour.Behaviour;
//import org.koekepan.herobrineproxy.session.IProxySessionNew;
//import org.koekepan.herobrineproxy.sps.SPSPacket;
//
//public class ServerExplosionPacketBehaviour implements Behaviour<Packet> {
//    private IProxySessionNew proxySession;
////    private IServerSession serverSession;
//
//    private ServerExplosionPacketBehaviour() {
//    }
//
//    public ServerExplosionPacketBehaviour(IProxySessionNew proxySession) {
//        this.proxySession = proxySession;
////        this.serverSession = serverSession;
//    }
//
//    @Override
//    public void process(Packet packet) {
//        ServerExplosionPacket serverExplosionPacket = (ServerExplosionPacket) packet;
//
////        ConsoleIO.println("Block changed at position: " + blockRecord.getPosition().getX() + ", " + blockRecord.getPosition().getY() + ", " + blockRecord.getPosition().getZ());
//        int x = (int) serverExplosionPacket.getX();
//        int y = (int) serverExplosionPacket.getY();
//        int z = (int) serverExplosionPacket.getZ();
//
//        SPSPacket spsPacket;
//        if (proxySession.getUsername().equals("ProxyListener2")) {
//            spsPacket = new SPSPacket(packet, "clientBound", x, z, 0);
//        } else {
//            spsPacket = new SPSPacket(packet, proxySession.getUsername(), (int) x, (int) z, 0);
//        }
//        proxySession.sendPacketToVASTnet_Client(spsPacket);
//    }
//}
