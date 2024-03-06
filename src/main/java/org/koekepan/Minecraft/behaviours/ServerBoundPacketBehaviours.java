package org.koekepan.Minecraft.behaviours;

import org.koekepan.Minecraft.behaviours.client.entity.ClientPlayerMovementPacketBehaviour;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Packet.BehaviourHandler;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientEnchantItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSpectatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerBoatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerVehiclePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientVehicleMovePacket;
import com.github.steveice10.mc.protocol.packet.login.client.EncryptionResponsePacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusPingPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusQueryPacket;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerBoundPacketBehaviours extends BehaviourHandler<Packet> {

	private EmulatedClientConnection emulatedClientConnection;
	private ForwardPacketBehaviour serverForwarder;

	public ServerBoundPacketBehaviours(EmulatedClientConnection clientInstance) {
		this.emulatedClientConnection = emulatedClientConnection;
	}
	

//	public void registerDefaultBehaviours(ISession clientSession) {
//		this.clientSession = clientSession;
//		Logger.log(this, Logger.Level.DEBUG, new String[]{"network", "behaviour"}, "Clearing behaviours before registering default behaviours");
//		clearBehaviours();
//		registerBehaviour(HandshakePacket.class, new ClientHandshakePacketBehaviour(this.clientSession, emulatedClientConnection));										// 0x06 Player Position And Look
//		registerBehaviour(LoginStartPacket.class, new ClientLoginStartPacketBehaviour(emulatedClientConnection));												// 0x01 Login Start
//	}
	
	
	public void registerForwardingBehaviour() {
		serverForwarder = new ForwardPacketBehaviour(emulatedClientConnection, true);
		registerBehaviour(EncryptionResponsePacket.class, serverForwarder);
		registerBehaviour(ClientTeleportConfirmPacket.class, serverForwarder);
		registerBehaviour(ClientTabCompletePacket.class, serverForwarder);
		registerBehaviour(ClientChatPacket.class, serverForwarder);
		registerBehaviour(ClientRequestPacket.class, serverForwarder);
		registerBehaviour(ClientSettingsPacket.class, serverForwarder);
		registerBehaviour(ClientConfirmTransactionPacket.class, serverForwarder);
		registerBehaviour(ClientEnchantItemPacket.class, serverForwarder);
		registerBehaviour(ClientWindowActionPacket.class, serverForwarder);
		registerBehaviour(ClientCloseWindowPacket.class, serverForwarder);
		registerBehaviour(ClientPluginMessagePacket.class, serverForwarder);
		registerBehaviour(ClientPlayerInteractEntityPacket.class, serverForwarder);
		registerBehaviour(ClientKeepAlivePacket.class, serverForwarder);

		/// Movement packets
		registerBehaviour(ClientPlayerPositionPacket.class, 		new ClientPlayerMovementPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ClientPlayerPositionRotationPacket.class, new ClientPlayerMovementPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ClientPlayerRotationPacket.class, 		new ClientPlayerMovementPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ClientPlayerMovementPacket.class, 		new ClientPlayerMovementPacketBehaviour(emulatedClientConnection));
		///

		/// More movement packets
		registerBehaviour(ClientVehicleMovePacket.class, serverForwarder);
		registerBehaviour(ClientSteerBoatPacket.class, serverForwarder);
		///

		registerBehaviour(ClientPlayerAbilitiesPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerActionPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerStatePacket.class, serverForwarder);
		registerBehaviour(ClientSteerVehiclePacket.class, serverForwarder);
		registerBehaviour(ClientResourcePackStatusPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerChangeHeldItemPacket.class, serverForwarder);
		registerBehaviour(ClientCreativeInventoryActionPacket.class, serverForwarder);
		registerBehaviour(ClientUpdateSignPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerSwingArmPacket.class, serverForwarder);
		registerBehaviour(ClientSpectatePacket.class, serverForwarder);
		registerBehaviour(ClientPlayerPlaceBlockPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerUseItemPacket.class, serverForwarder);
		
		registerBehaviour(StatusQueryPacket.class, serverForwarder);
		registerBehaviour(StatusPingPacket.class, serverForwarder);
	}
}
