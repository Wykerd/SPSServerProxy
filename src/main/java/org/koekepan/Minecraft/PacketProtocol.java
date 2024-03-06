package org.koekepan.Minecraft;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerDisplayScoreboardPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerScoreboardObjectivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerTeamPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerUpdateScorePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.*;
import com.github.steveice10.mc.protocol.packet.login.client.EncryptionResponsePacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.login.server.EncryptionRequestPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusPingPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusQueryPacket;
import com.github.steveice10.mc.protocol.packet.status.server.StatusPongPacket;
import com.github.steveice10.mc.protocol.packet.status.server.StatusResponsePacket;
import org.koekepan.VAST.CustomPackets.EstablishConnectionPacket;

// this class describes the packet protocol that is used by the proxy and tracks protocol state changes.
// one instance per client or server connection.
public class PacketProtocol extends MinecraftProtocol {

    public PacketProtocol() {
        super(SubProtocol.STATUS);
        init();
    }

    public void init() {
        this.register(0x00, HandshakePacket.class);

        this.register(0x01, LoginDisconnectPacket.class);
        this.register(0x02, EncryptionRequestPacket.class);
        this.register(0x03, LoginSuccessPacket.class);
        this.register(0x04, LoginSetCompressionPacket.class);
        this.register(0x05, LoginStartPacket.class);
        this.register(0x06, EncryptionResponsePacket.class);


        this.register(0x07, ServerSpawnObjectPacket.class);
        this.register(0x08, ServerSpawnExpOrbPacket.class);
        this.register(0x09, ServerSpawnGlobalEntityPacket.class);
        this.register(0x0A, ServerSpawnMobPacket.class);
        this.register(0x0B, ServerSpawnPaintingPacket.class);
        this.register(0x0C, ServerSpawnPlayerPacket.class);
        this.register(0x0D, ServerEntityAnimationPacket.class);
        this.register(0x0E, ServerStatisticsPacket.class);
        this.register(0x0F, ServerBlockBreakAnimPacket.class);
        this.register(0x10, ServerUpdateTileEntityPacket.class);
        this.register(0x11, ServerBlockValuePacket.class);
        this.register(0x12, ServerBlockChangePacket.class);
        this.register(0x13, ServerBossBarPacket.class);
        this.register(0x14, ServerDifficultyPacket.class);
        this.register(0x15, ServerTabCompletePacket.class);
        this.register(0x16, ServerChatPacket.class);
        this.register(0x17, ServerMultiBlockChangePacket.class);
        this.register(0x18, ServerConfirmTransactionPacket.class);
        this.register(0x19, ServerCloseWindowPacket.class);
        this.register(0x1A, ServerOpenWindowPacket.class);
        this.register(0x1B, ServerWindowItemsPacket.class);
        this.register(0x1C, ServerWindowPropertyPacket.class);
        this.register(0x1D, ServerSetSlotPacket.class);
        this.register(0x1E, ServerSetCooldownPacket.class);
        this.register(0x1F, ServerPluginMessagePacket.class);
        this.register(0x20, ServerPlaySoundPacket.class);
        this.register(0x21, ServerDisconnectPacket.class);
        this.register(0x22, ServerEntityStatusPacket.class);
        this.register(0x23, ServerExplosionPacket.class);
        this.register(0x24, ServerUnloadChunkPacket.class);
        this.register(0x25, ServerNotifyClientPacket.class);
        this.register(0x26, ServerKeepAlivePacket.class);
        this.register(0x27, ServerChunkDataPacket.class);
        this.register(0x28, ServerPlayEffectPacket.class);
        this.register(0x29, ServerSpawnParticlePacket.class);
        this.register(0x2A, ServerJoinGamePacket.class);
        this.register(0x2B, ServerMapDataPacket.class);
        this.register(0x2C, ServerEntityPositionPacket.class);
        this.register(0x2D, ServerEntityPositionRotationPacket.class);
        this.register(0x2E, ServerEntityRotationPacket.class);
        this.register(0x2F, ServerEntityMovementPacket.class);
        this.register(0x30, ServerVehicleMovePacket.class);
        this.register(0x31, ServerOpenTileEntityEditorPacket.class);
        this.register(0x32, ServerPlayerAbilitiesPacket.class);
        this.register(0x33, ServerCombatPacket.class);
        this.register(0x34, ServerPlayerListEntryPacket.class);
        this.register(0x35, ServerPlayerPositionRotationPacket.class);
        this.register(0x36, ServerPlayerUseBedPacket.class);
        this.register(0x37, ServerEntityDestroyPacket.class);
        this.register(0x38, ServerEntityRemoveEffectPacket.class);
        this.register(0x39, ServerResourcePackSendPacket.class);
        this.register(0x3A, ServerRespawnPacket.class);
        this.register(0x3B, ServerEntityHeadLookPacket.class);
        this.register(0x3C, ServerWorldBorderPacket.class);
        this.register(0x3D, ServerSwitchCameraPacket.class);
        this.register(0x3E, ServerPlayerChangeHeldItemPacket.class);
        this.register(0x3F, ServerDisplayScoreboardPacket.class);
        this.register(0x40, ServerEntityMetadataPacket.class);
        this.register(0x41, ServerEntityAttachPacket.class);
        this.register(0x42, ServerEntityVelocityPacket.class);
        this.register(0x43, ServerEntityEquipmentPacket.class);
        this.register(0x44, ServerPlayerSetExperiencePacket.class);
        this.register(0x45, ServerPlayerHealthPacket.class);
        this.register(0x46, ServerScoreboardObjectivePacket.class);
        this.register(0x47, ServerEntitySetPassengersPacket.class);
        this.register(0x48, ServerTeamPacket.class);
        this.register(0x49, ServerUpdateScorePacket.class);
        this.register(0x4A, ServerSpawnPositionPacket.class);
        this.register(0x4B, ServerUpdateTimePacket.class);
        this.register(0x4C, ServerTitlePacket.class);
        this.register(0x4D, ServerPlayBuiltinSoundPacket.class);
        this.register(0x4E, ServerPlayerListDataPacket.class);
        this.register(0x4F, ServerEntityCollectItemPacket.class);
        this.register(0x50, ServerEntityTeleportPacket.class);
        this.register(0x51, ServerEntityPropertiesPacket.class);
        this.register(0x52, ServerEntityEffectPacket.class);

        this.register(0x53, ClientTeleportConfirmPacket.class);
        this.register(0x54, ClientTabCompletePacket.class);
        this.register(0x55, ClientChatPacket.class);
        this.register(0x56, ClientRequestPacket.class);
        this.register(0x57, ClientSettingsPacket.class);
        this.register(0x58, ClientConfirmTransactionPacket.class);
        this.register(0x59, ClientEnchantItemPacket.class);
        this.register(0x5A, ClientWindowActionPacket.class);
        this.register(0x5B, ClientCloseWindowPacket.class);
        this.register(0x5C, ClientPluginMessagePacket.class);
        this.register(0x5D, ClientPlayerInteractEntityPacket.class);
        this.register(0x5E, ClientKeepAlivePacket.class);
        this.register(0x5F, ClientPlayerPositionPacket.class);
        this.register(0x60, ClientPlayerPositionRotationPacket.class);
        this.register(0x61, ClientPlayerRotationPacket.class);
        this.register(0x62, ClientPlayerMovementPacket.class);
        this.register(0x63, ClientVehicleMovePacket.class);
        this.register(0x64, ClientSteerBoatPacket.class);
        this.register(0x65, ClientPlayerAbilitiesPacket.class);
        this.register(0x66, ClientPlayerActionPacket.class);
        this.register(0x67, ClientPlayerStatePacket.class);
        this.register(0x68, ClientSteerVehiclePacket.class);
        this.register(0x69, ClientResourcePackStatusPacket.class);
        this.register(0x6A, ClientPlayerChangeHeldItemPacket.class);
        this.register(0x6B, ClientCreativeInventoryActionPacket.class);
        this.register(0x6C, ClientUpdateSignPacket.class);
        this.register(0x6D, ClientPlayerSwingArmPacket.class);
        this.register(0x6E, ClientSpectatePacket.class);
        this.register(0x6F, ClientPlayerPlaceBlockPacket.class);
        this.register(0x70, ClientPlayerUseItemPacket.class);

        this.register(0x71, StatusResponsePacket.class);
        this.register(0x72, StatusPongPacket.class);

        this.register(0x73, StatusQueryPacket.class);
        this.register(0x74, StatusPingPacket.class);

        this.register(0x75, EstablishConnectionPacket.class);
    }

}