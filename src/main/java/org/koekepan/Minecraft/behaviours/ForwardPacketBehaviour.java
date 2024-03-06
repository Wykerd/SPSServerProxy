package org.koekepan.Minecraft.behaviours;

import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Connection.PacketSender;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;

import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.VAST.Packet.SPSPacket;

public class ForwardPacketBehaviour implements Behaviour<Packet> {

	private EmulatedClientConnection emulatedClientConnection;
	private boolean toServer;
	
	@SuppressWarnings("unused")
	private ForwardPacketBehaviour() {}
	
	
	public ForwardPacketBehaviour(EmulatedClientConnection emulatedClientConnection, boolean toServer) {
		this.emulatedClientConnection = emulatedClientConnection;
		this.toServer = toServer;
	}

	
	@Override
	public void process(Packet packet) {
		if (toServer) {
		} else {
			if (!(this.emulatedClientConnection.getUsername().equals("ProxyListener2"))) {

				if (packet.getClass().getSimpleName().equals("LoginSetCompressionPacket")) { //TODO: This should be Player specific, part of login process
//					ConsoleIO.println("ForwardPacketBehaviour.process: " + packet.getClass().getSimpleName());
					emulatedClientConnection.getPacketSender().removePacket(packet);
					return;
				}

				SPSPacket spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), (int) emulatedClientConnection.getXPosition(), (int) emulatedClientConnection.getZPosition(), 0, emulatedClientConnection.getUsername());
				PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
			}
		}

		try {
			PacketWrapper.setProcessed(packet, true);
		} catch (Exception e) {
			System.out.println("ForwardPacketBehaviour for packet: <" + packet.getClass().getSimpleName() + "> failed setting processed: <" + e.getMessage() + ">");
		}
	}
}
