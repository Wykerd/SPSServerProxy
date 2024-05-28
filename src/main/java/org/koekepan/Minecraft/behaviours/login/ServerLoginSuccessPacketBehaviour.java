package org.koekepan.Minecraft.behaviours.login;

import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.util.UUID;

public class ServerLoginSuccessPacketBehaviour implements Behaviour<Packet> {
		
	private EmulatedClientConnection emulatedClientConnection;
	
	@SuppressWarnings("unused")
	private ServerLoginSuccessPacketBehaviour() {}
	
	
	public ServerLoginSuccessPacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
		this.emulatedClientConnection = emulatedClientConnection;
	}

	
	@Override
	public void process(Packet packet) {
		LoginSuccessPacket loginSuccessPacket = (LoginSuccessPacket)packet;

//		Logger.log(this, Logger.Level.DEBUG, new String[]{"serverBound", "behaviour"},"ServerLoginSuccessPacketBehaviour::process => Player \""+loginSuccessPacket.getProfile().getName()+"\" has successfully logged into the server");

		String username = loginSuccessPacket.getProfile().getName();
		UUID UUID = loginSuccessPacket.getProfile().getId();

//		try {
//			new PlayerTracker(username, UUID);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}

		SPSPacket spsPacket = new SPSPacket(packet, loginSuccessPacket.getProfile().getName(), 1, 1, 2000, "clientBound"); // TODO: should be published either globally or to a new channel just for login?
//		emulatedClientConnection.sendPacketToVASTnet_Client(spsPacket);
		PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
		PacketWrapper.setProcessed(packet, true);
	}
}