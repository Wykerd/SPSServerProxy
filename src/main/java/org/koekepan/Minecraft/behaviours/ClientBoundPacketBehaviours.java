package org.koekepan.Minecraft.behaviours;

import org.koekepan.Minecraft.behaviours.server.ServerJoinGamePacketBehaviour;
import org.koekepan.Minecraft.behaviours.server.ServerPluginMessagePacketBehaviour;
import org.koekepan.Minecraft.behaviours.server.entity.*;
import org.koekepan.Minecraft.behaviours.server.world.*;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.CustomPackets.PINGPONG;
import org.koekepan.VAST.Packet.BehaviourHandler;
import org.koekepan.Minecraft.behaviours.login.ServerLoginSuccessPacketBehaviour;
import org.koekepan.Minecraft.behaviours.server.serverpackets.ServerDisconnectPacketBehaviour;
import org.koekepan.Minecraft.behaviours.server.spatial.ServerBlockValuePacketBehaviour;
import org.koekepan.Minecraft.behaviours.server.spatial.ServerPlayEffectPacketBehaviour;
import org.koekepan.Minecraft.behaviours.server.spatial.ServerPlayerUseBedPacketBehaviour;

import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.*;
import com.github.steveice10.mc.protocol.packet.login.server.*;
import com.github.steveice10.mc.protocol.packet.status.server.*;
import com.github.steveice10.packetlib.packet.Packet;

public class ClientBoundPacketBehaviours extends BehaviourHandler<Packet> {

	private final EmulatedClientConnection emulatedClientConnection;
//	private IServerSession serverSession;
	private ForwardPacketBehaviour clientForwarder;

	public ClientBoundPacketBehaviours(EmulatedClientConnection emulatedClientConnection) {
		this.emulatedClientConnection = emulatedClientConnection;
//		this.serverSession = serverSession;
	}
	

	/*public void registerDefaultBehaviours() {
		clearBehaviours();
		this.registerForwardingBehaviour();
	}*/
	
	
	public void registerForwardingBehaviour() {
		clearBehaviours();
		clientForwarder = new ForwardPacketBehaviour(emulatedClientConnection, false);
		registerBehaviour(LoginDisconnectPacket.class, clientForwarder);
		registerBehaviour(EncryptionRequestPacket.class, clientForwarder);
		registerBehaviour(LoginSuccessPacket.class, 				new ServerLoginSuccessPacketBehaviour(emulatedClientConnection));
		registerBehaviour(LoginSetCompressionPacket.class, clientForwarder);

		/// Spawn entity packets
		registerBehaviour(ServerSpawnObjectPacket.class, 			new ServerSpawnObjectPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerSpawnExpOrbPacket.class, 			new ServerSpawnExpOrbPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerSpawnGlobalEntityPacket.class, 		new ServerSpawnGlobalEntityPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerSpawnMobPacket.class, 				new ServerSpawnMobPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerSpawnPaintingPacket.class, 			new ServerSpawnPaintingPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerSpawnPlayerPacket.class, 			new ServerSpawnPlayerPacketBehaviour(emulatedClientConnection));
		///
		registerBehaviour(ServerEntityAnimationPacket.class,		new ServerEntityAnimationPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerStatisticsPacket.class, clientForwarder);
		registerBehaviour(ServerBlockBreakAnimPacket.class, 		new ServerBlockBreakAnimPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerUpdateTileEntityPacket.class, 		new ServerUpdateTileEntityPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerBlockValuePacket.class, 			new ServerBlockValuePacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerBlockChangePacket.class, 			new ServerBlockChangePacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerBossBarPacket.class, clientForwarder);
		registerBehaviour(ServerDifficultyPacket.class, clientForwarder);
		registerBehaviour(ServerTabCompletePacket.class, clientForwarder);
		registerBehaviour(ServerChatPacket.class, clientForwarder);

		registerBehaviour(ServerMultiBlockChangePacket.class, 		new ServerMultiBlockChangePacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerConfirmTransactionPacket.class, clientForwarder);
		registerBehaviour(ServerCloseWindowPacket.class, clientForwarder);
		registerBehaviour(ServerOpenWindowPacket.class, clientForwarder);
		registerBehaviour(ServerWindowItemsPacket.class, clientForwarder);
		registerBehaviour(ServerWindowPropertyPacket.class, clientForwarder);
		registerBehaviour(ServerSetSlotPacket.class, clientForwarder);
		registerBehaviour(ServerSetCooldownPacket.class, clientForwarder);

		registerBehaviour(ServerPlaySoundPacket.class, 				new ServerPlaySoundPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerDisconnectPacket.class, 			new ServerDisconnectPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerEntityStatusPacket.class, 			new ServerEntityStatusPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerExplosionPacket.class, clientForwarder); // PS because of explosion vector.
		registerBehaviour(ServerUnloadChunkPacket.class, clientForwarder);
		registerBehaviour(ServerNotifyClientPacket.class, clientForwarder);
		registerBehaviour(ServerKeepAlivePacket.class, clientForwarder);

		registerBehaviour(ServerChunkDataPacket.class, 				new ServerChunkDataPacketBehaviour(emulatedClientConnection));
		
		registerBehaviour(ServerPlayEffectPacket.class, 			new ServerPlayEffectPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerSpawnParticlePacket.class, 			new ServerSpawnParticlePacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerJoinGamePacket.class, 				new ServerJoinGamePacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerMapDataPacket.class, clientForwarder);

		/// Movement packets ///
		registerBehaviour(ServerEntityPositionPacket.class, 		new ServerEntityMovementPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerEntityPositionRotationPacket.class, new ServerEntityMovementPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerEntityRotationPacket.class, 		new ServerEntityMovementPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerEntityMovementPacket.class, 		new ServerEntityMovementPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerEntityHeadLookPacket.class, 		new ServerEntityHeadLookPacketBehaviour(emulatedClientConnection));
		////////////////////////

		registerBehaviour(ServerVehicleMovePacket.class, clientForwarder);
		registerBehaviour(ServerOpenTileEntityEditorPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerAbilitiesPacket.class, clientForwarder);
		registerBehaviour(ServerCombatPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerListEntryPacket.class, clientForwarder);

		registerBehaviour(ServerPlayerUseBedPacket.class, 			new ServerPlayerUseBedPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerEntityDestroyPacket.class, clientForwarder);
		registerBehaviour(ServerEntityRemoveEffectPacket.class, clientForwarder);
		registerBehaviour(ServerResourcePackSendPacket.class, clientForwarder);
		registerBehaviour(ServerRespawnPacket.class, clientForwarder);

		registerBehaviour(ServerWorldBorderPacket.class, clientForwarder);
		registerBehaviour(ServerSwitchCameraPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerChangeHeldItemPacket.class, clientForwarder);
		registerBehaviour(ServerDisplayScoreboardPacket.class, clientForwarder);

		registerBehaviour(ServerEntityMetadataPacket.class, 		new ServerEntityMetadataPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerEntityAttachPacket.class,			new ServerEntityAttachPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerEntityVelocityPacket.class, 		new ServerEntityVelocityPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerEntityEquipmentPacket.class, 		new ServerEntityEquipmentPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerPlayerPositionRotationPacket.class, new ServerPlayerPositionRotationPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerPlayerSetExperiencePacket.class, clientForwarder);
		registerBehaviour(ServerPlayerHealthPacket.class, clientForwarder);
		registerBehaviour(ServerScoreboardObjectivePacket.class, clientForwarder);

		registerBehaviour(ServerEntitySetPassengersPacket.class, 	new ServerEntitySetPassengersPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerTeamPacket.class, clientForwarder);
		registerBehaviour(ServerUpdateScorePacket.class, clientForwarder);
		registerBehaviour(ServerSpawnPositionPacket.class, clientForwarder);
		registerBehaviour(ServerUpdateTimePacket.class, clientForwarder);
		registerBehaviour(ServerTitlePacket.class, clientForwarder);
		registerBehaviour(ServerPlayBuiltinSoundPacket.class, clientForwarder);
		registerBehaviour(ServerPlayerListDataPacket.class, clientForwarder);

		registerBehaviour(ServerEntityCollectItemPacket.class, 		new ServerEntityCollectItemPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerEntityTeleportPacket.class, 		new ServerEntityTeleportPacketBehaviour(emulatedClientConnection));
		registerBehaviour(ServerEntityPropertiesPacket.class, 		new ServerEntityPropertiesPacketBehaviour(emulatedClientConnection));

		registerBehaviour(ServerEntityEffectPacket.class, clientForwarder);
		registerBehaviour(StatusResponsePacket.class, clientForwarder);
		registerBehaviour(StatusPongPacket.class, clientForwarder);
		
		registerBehaviour(ServerPluginMessagePacket.class, 			new ServerPluginMessagePacketBehaviour(emulatedClientConnection));

		registerBehaviour(PINGPONG.class, new PINGPONGBehaviour(emulatedClientConnection));
	}
	
	
//	public void registerMigrationBehaviour() {
//		this.clearBehaviours();
//		clientForwarder = new ForwardPacketBehaviour(proxySession, false);
//		registerBehaviour(LoginDisconnectPacket.class, clientForwarder);
//		registerBehaviour(EncryptionRequestPacket.class, clientForwarder);
//		registerBehaviour(LoginSuccessPacket.class, new MigrateLoginSuccessPacketBehaviour(proxySession) );
//		registerBehaviour(LoginSetCompressionPacket.class, new LoginSetCompressionPacketBehaviour());
//
// 		registerBehaviour(ServerSpawnObjectPacket.class, clientForwarder);
//		registerBehaviour(ServerSpawnExpOrbPacket.class, clientForwarder);
//		registerBehaviour(ServerSpawnGlobalEntityPacket.class, clientForwarder);
//		registerBehaviour(ServerSpawnMobPacket.class, clientForwarder);
//		registerBehaviour(ServerSpawnPaintingPacket.class, clientForwarder);
//		registerBehaviour(ServerSpawnPlayerPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityAnimationPacket.class, clientForwarder);
//		registerBehaviour(ServerStatisticsPacket.class, clientForwarder);
//		registerBehaviour(ServerBlockBreakAnimPacket.class, clientForwarder);
//		registerBehaviour(ServerUpdateTileEntityPacket.class, clientForwarder);
//		registerBehaviour(ServerBlockValuePacket.class, clientForwarder);
//		registerBehaviour(ServerBlockChangePacket.class, clientForwarder);
//		registerBehaviour(ServerBossBarPacket.class, clientForwarder);
//		registerBehaviour(ServerDifficultyPacket.class, clientForwarder);
//		registerBehaviour(ServerTabCompletePacket.class, clientForwarder);
//		registerBehaviour(ServerChatPacket.class, clientForwarder);
//		registerBehaviour(ServerMultiBlockChangePacket.class, clientForwarder);
//		registerBehaviour(ServerConfirmTransactionPacket.class, clientForwarder);
//		registerBehaviour(ServerCloseWindowPacket.class, clientForwarder);
//		registerBehaviour(ServerOpenWindowPacket.class, clientForwarder);
//		registerBehaviour(ServerWindowItemsPacket.class, clientForwarder);
//		registerBehaviour(ServerWindowPropertyPacket.class, clientForwarder);
//		registerBehaviour(ServerSetSlotPacket.class, clientForwarder);
//		registerBehaviour(ServerSetCooldownPacket.class, clientForwarder);
//		registerBehaviour(ServerPlaySoundPacket.class, clientForwarder);
//		registerBehaviour(ServerDisconnectPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityStatusPacket.class, clientForwarder);
//		registerBehaviour(ServerExplosionPacket.class, clientForwarder);
//		registerBehaviour(ServerUnloadChunkPacket.class, clientForwarder);
//		registerBehaviour(ServerNotifyClientPacket.class, clientForwarder);
//		registerBehaviour(ServerKeepAlivePacket.class, clientForwarder);
//		registerBehaviour(ServerChunkDataPacket.class, clientForwarder);
//		registerBehaviour(ServerPlayEffectPacket.class, clientForwarder);
//		registerBehaviour(ServerSpawnParticlePacket.class, clientForwarder);
//		registerBehaviour(ServerJoinGamePacket.class, new MigrateJoinGamePacketBehaviour(proxySession, serverSession));
//		registerBehaviour(ServerMapDataPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityPositionPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityPositionRotationPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityRotationPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityMovementPacket.class, clientForwarder);
//		registerBehaviour(ServerVehicleMovePacket.class, clientForwarder);
//		registerBehaviour(ServerOpenTileEntityEditorPacket.class, clientForwarder);
//		registerBehaviour(ServerPlayerAbilitiesPacket.class, clientForwarder);
//		registerBehaviour(ServerCombatPacket.class, clientForwarder);
//		registerBehaviour(ServerPlayerListEntryPacket.class, clientForwarder);
//		registerBehaviour(ServerPlayerPositionRotationPacket.class, new ServerPlayerPositionPacketBehaviour(proxySession));
//		registerBehaviour(ServerPlayerUseBedPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityDestroyPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityRemoveEffectPacket.class, clientForwarder);
//		registerBehaviour(ServerResourcePackSendPacket.class, clientForwarder);
//		registerBehaviour(ServerRespawnPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityHeadLookPacket.class, clientForwarder);
//		registerBehaviour(ServerWorldBorderPacket.class, clientForwarder);
//		registerBehaviour(ServerSwitchCameraPacket.class, clientForwarder);
//		registerBehaviour(ServerPlayerChangeHeldItemPacket.class, clientForwarder);
//		registerBehaviour(ServerDisplayScoreboardPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityMetadataPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityAttachPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityVelocityPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityEquipmentPacket.class, clientForwarder);
//		registerBehaviour(ServerPlayerSetExperiencePacket.class, clientForwarder);
//		registerBehaviour(ServerPlayerHealthPacket.class, clientForwarder);
//		registerBehaviour(ServerScoreboardObjectivePacket.class, clientForwarder);
//		registerBehaviour(ServerEntitySetPassengersPacket.class, clientForwarder);
//		registerBehaviour(ServerTeamPacket.class, clientForwarder);
//		registerBehaviour(ServerUpdateScorePacket.class, clientForwarder);
//		registerBehaviour(ServerSpawnPositionPacket.class, clientForwarder);
//		registerBehaviour(ServerUpdateTimePacket.class, clientForwarder);
//		registerBehaviour(ServerTitlePacket.class, clientForwarder);
//		registerBehaviour(ServerPlayBuiltinSoundPacket.class, clientForwarder);
//		registerBehaviour(ServerPlayerListDataPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityCollectItemPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityTeleportPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityPropertiesPacket.class, clientForwarder);
//		registerBehaviour(ServerEntityEffectPacket.class, clientForwarder);
//
//		registerBehaviour(StatusResponsePacket.class, clientForwarder);
//		registerBehaviour(StatusPongPacket.class, clientForwarder);
//
//		registerBehaviour(ServerPluginMessagePacket.class, new ServerPluginMessagePacketBehaviour(proxySession));
//	}
}
