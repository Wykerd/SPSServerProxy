package org.koekepan.Minecraft.behaviours.server;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.packetlib.packet.Packet;
//import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.koekepan.App;
import org.koekepan.Minecraft.ChunkPosition;
import org.koekepan.VAST.Connection.EmulatedClientConnection;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerPluginMessagePacketBehaviour implements Behaviour<Packet> {
	private EmulatedClientConnection emulatedClientConnection;

	private List<ChunkPosition> current_positions = new ArrayList<>();
	
	@SuppressWarnings("unused")
	private ServerPluginMessagePacketBehaviour() {}
	
	
	public ServerPluginMessagePacketBehaviour(EmulatedClientConnection emulatedClientConnection) {
		this.emulatedClientConnection = emulatedClientConnection;
	}

	
	@Override
	public void process(Packet packet) {
//		PacketSender.removePacketFromQueue(packet);
//		emulatedClientConnection.getPacketSender().removePacket(packet);
		PacketWrapper packetWrapper = PacketWrapper.getPacketWrapper(packet);
		SPSPacket spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
		packetWrapper.setSPSPacket(spsPacket);
		packetWrapper.isProcessed = true;
//		PacketWrapper.setProcessed(packet, true);

		ServerPluginMessagePacket pluginMessagePacket = (ServerPluginMessagePacket)packet;

//		ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => PluginMessagePacket for channel <"+pluginMessagePacket.getChannel()+">");
		if (pluginMessagePacket.getChannel().equals("Koekepan|migrate")) {
			System.out.println("ServerPluginMessagePacketBehaviour::process => Received a migration message for client <"+emulatedClientConnection.getUsername()+"> to migrate to server <" + pluginMessagePacket.toString()  +">");
//			byte[] payload = pluginMessagePacket.getData();
//			String hostname = this.readStringFromData(payload);
//			ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => Received a migration message for client <"+proxySession.getUsername()+"> to migrate to server <"+hostname+">");
//			proxySession.migrate(hostname, proxySession.getServerPort()); 
		} else if (pluginMessagePacket.getChannel().equals("Koekepan|kick")) {
//				byte[] payload = pluginMessagePacket.getData();
//				String reason = this.readStringFromData(payload);
//				ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => Received a kick message for client <"+proxySession.getUsername()+"> with reason <"+reason+">");
//				if (proxySession.isConnected()) {
//					proxySession.disconnect();
//				}
		} else if (pluginMessagePacket.getChannel().equals("Koekepan|latency")) {
			byte[] payload = pluginMessagePacket.getData();
//			this.echoLatencyPacket(emulatedClientConnection, "Koekepan|latency", payload);
			//System.out.println("ServerPluginMessagePacketBehaviour::process => Received latency measurement packet for client <"+client.getName()+">");	
		} else if (pluginMessagePacket.getChannel().equals("Koekepan|partition")) {
//			System.out.println("Received partition info!");
//			ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => Received a partition message for client <"+ emulatedClientConnection.getUsername()+">");
			try {

				byte[] payload = pluginMessagePacket.getData();
				ByteArrayInputStream bis = new ByteArrayInputStream(payload);
				DataInputStream in = new DataInputStream(bis);
			
				// Read location data
				double x = in.readDouble();
				double y = in.readDouble();
				double z = in.readDouble();
				float pitch = in.readFloat();
				float yaw = in.readFloat();
			
				// Read volume data
				int length = in.readInt();

				double[] xPoints = new double[length];
				double[] yPoints = new double[length];
				for (int i = 0; i < length; i++) {
					xPoints[i] = in.readDouble();
					yPoints[i] = in.readDouble();
				}
				
				// Read world UID data
				long MSB = in.readLong();
				long LSB = in.readLong();
				UUID worldUID = new UUID(MSB, LSB);
				
//				ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => <x,y,z,pitch,yaw> = <"+x+","+y+","+z+","+pitch+","+yaw+">");

				StringBuilder returnValue = new StringBuilder();
				for (int i = 0; i < length; i++) {
					returnValue.append("(").append(xPoints[i]).append(",").append(yPoints[i]).append(")\n");
				}

//				System.out.println("ServerPluginMessagePacketBehaviour::process => Volume: "+returnValue);

				//
				List<ChunkPosition> new_positions = new ArrayList<>();
				// Convert xPoints and yPoints into ChunkPosition objects and add them to the list
				for (int i = 0; i < xPoints.length; i++) {
					new_positions.add(new ChunkPosition((int)xPoints[i], (int)yPoints[i]));
				}

//				if (areChunkPositionsEqual(new_positions, current_positions)) {
				if (new_positions.equals(current_positions)) {
//					System.out.println("ServerPluginMessagePacketBehaviour::process => Positions are equal, not subscribing again");
					return;
				} else {
					current_positions = new_positions;
					System.out.println("ServerPluginMessagePacketBehaviour::process => Positions are not equal, subscribing again");
				}

				// Call the subscribePolygon function


				App.getVastConnection().unsubscribe("serverBound");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                App.getVastConnection().subscribePolygon(new_positions);


//				ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => Volume: "+returnValue);
				
//				ConsoleIO.println("ServerPluginMessagePacketBehaviour::process => World UUID = <"+worldUID+">");

//				SPSPartition partition = new SPSPartition(xPoints, yPoints);
//				emulatedClientConnection.setVoronoiPartition(partition);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
	}

	private boolean areChunkPositionsEqual(List<ChunkPosition> list1, List<ChunkPosition> list2) {
		if (list1.size() != list2.size()) {
			return false;
		}

		for (int i = 0; i < list1.size(); i++) {
			ChunkPosition pos1 = list1.get(i);
			ChunkPosition pos2 = list2.get(i);

			if (pos1.getX() != pos2.getX() || pos1.getZ() != pos2.getZ()) {
				return false;
			}
		}

		return true;
	}
	
	/**
     * Reads a compressed int from the buffer. To do so it maximally reads 5 byte-sized chunks whose most significant
     * bit dictates whether another byte should be read.
     */
//    private int readVarIntFromBuffer(ByteBuf buffer)
//    {
//        int var1 = 0;
//        int var2 = 0;
//        byte var3;
//
//        do
//        {
//            var3 = buffer.readByte();
//            var1 |= (var3 & 127) << var2++ * 7;
//
//            if (var2 > 5)
//            {
//                throw new RuntimeException("VarInt too big");
//            }
//        }
//        while ((var3 & 128) == 128);
//
//        return var1;
//    }
//
//
//	private String readStringFromData(byte[] data) {
//		ByteBuf buffer = Unpooled.wrappedBuffer(data);
//
//        int length = this.readVarIntFromBuffer(buffer);
//		byte[] readBytes = new byte[length];
//
//    	buffer.readBytes(readBytes);
//		String message = new String(readBytes, Charsets.UTF_8);
//        return message;
//	}
//
//
//	private void echoLatencyPacket(EmulatedClientConnection proxySession, String channel, byte[] payload) {
//		long currentTime = System.currentTimeMillis();
//		LatencyData data = new LatencyData();
//		data.reconstructFromBytes(payload);
//		if (data.clientSentTime == -1) {
//			data.clientSentTime = currentTime;
//			ClientPluginMessagePacket echoPacket = new ClientPluginMessagePacket(channel, data.convertToBytes());
//			proxySession.sendPacketToServer(echoPacket);
//		} else {
//			data.clientReceiveTime = currentTime;
//			//log.info("HerobrineMigrationListener::echoLatencyPacket => CurrentTime <"+currentTime+"> Server <"+data.serverSentTime+","+data.serverReceiveTime+"> Serverside RTT <"+(data.serverReceiveTime - data.serverSentTime)+"> Client <"+data.clientSentTime+","+data.clientReceiveTime+"> Clientside Latency <"+(data.clientReceiveTime - data.clientSentTime)+">");
//		}
//	}
}
