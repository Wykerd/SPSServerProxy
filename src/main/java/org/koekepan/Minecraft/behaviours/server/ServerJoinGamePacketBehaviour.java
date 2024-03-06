package org.koekepan.Minecraft.behaviours.server;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

public class ServerJoinGamePacketBehaviour implements Behaviour<Packet> {
		
	private EmulatedClientConnection emulatedClientConnection;
//	private IServerSession serverSession;
	
	@SuppressWarnings("unused")
	private ServerJoinGamePacketBehaviour() {}
	
	
	public ServerJoinGamePacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
		this.emulatedClientConnection = emulatedClientConnection;
	}

	
	@Override
	public void process(Packet packet) {
		ServerJoinGamePacket serverJoinPacket = (ServerJoinGamePacket) packet;

		System.out.println("ServerJoinGamePacketBehaviour::process => player \""+ emulatedClientConnection.getUsername()+"\" with entityID <"+serverJoinPacket.getEntityId()+"> has successfully joined world");

		if (EmulatedClientConnection.getClientInstanceByUsername(emulatedClientConnection.getUsername()) != null) {
			EmulatedClientConnection.getClientInstanceByUsername(emulatedClientConnection.getUsername()).setEntityID(serverJoinPacket.getEntityId());
		} else {
			System.out.println("ServerJoinGamePacketBehaviour::process => (ERROR) No client instance found for player \""+emulatedClientConnection.getUsername()+"\"");
		}

//		double x = PlayerTracker.getXByUsername(proxySession.getUsername());
//		double z = PlayerTracker.getZByUsername(proxySession.getUsername());

		double x = 0;
		double z = 0;


		SPSPacket spsPacket = new SPSPacket(packet, "Herobrine", (int)x, (int)z, 10000, emulatedClientConnection.getUsername()); // TODO: This should be Player specific, part of login process
		PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
		PacketWrapper.setProcessed(packet, true);

		emulatedClientConnection.setConnected(true);
	}
}