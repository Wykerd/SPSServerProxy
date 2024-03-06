package org.koekepan.Minecraft.behaviours.client.entity;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;

public class ClientPlayerMovementPacketBehaviour implements Behaviour<Packet> {
    private EmulatedClientConnection emulatedClientConnection;

    private ClientPlayerMovementPacketBehaviour() {
    }

    public ClientPlayerMovementPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
        this.emulatedClientConnection = emulatedClientConnection;
//        this.serverSession = serverSession;
    }

    @Override
    public void process(Packet packet) {
//        ConsoleIO.println("Processing clientPlayerMovementPacket");

        ClientPlayerMovementPacket clientPlayerMovementPacket = (ClientPlayerMovementPacket) packet;

        double x = clientPlayerMovementPacket.getX();
        double y = clientPlayerMovementPacket.getY();
        double z = clientPlayerMovementPacket.getZ();


        String packetClassName = clientPlayerMovementPacket.getClass().getSimpleName();

        switch (packetClassName) {
            case "ClientPlayerPositionPacket":

            case "ClientPlayerMovementPacket":

            case "ClientPlayerPositionRotationPacket":
                // Handle ClientPlayerPositionRotationPacket logic
                // Handle ClientPlayerMovementPacket logic
                // Handle ClientPlayerPositiintonPacket logic
                emulatedClientConnection.movePlayer(x, y, z, false);
                break;
            case "ClientPlayerRotationPacket":
                // Handle ClientPlayerRotationPacket logic
                break;
            default:
                break;
        }

//        ((SPSToServerProxy) emulatedClientConnection).sendPacketToServer(packet, true);
        try {
            PacketWrapper.setProcessed(packet, true); // Server/Clientbound packets are assigned in ClientConnectedInstance and on Vast Publication
        } catch (Exception e) {
            System.out.println("ClientPlayerMovementPacketBehaviour for packet: <" + packet.getClass().getSimpleName() + "> failed setting processed: <" + e.getMessage() + ">");
        }
    }
}
