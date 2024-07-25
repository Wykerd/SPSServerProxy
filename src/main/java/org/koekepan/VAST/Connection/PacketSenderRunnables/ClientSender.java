package org.koekepan.VAST.Connection.PacketSenderRunnables;

import com.github.steveice10.packetlib.Session;
import org.koekepan.Performance.PacketCapture;
import org.koekepan.VAST.Connection.PacketSender;
import org.koekepan.VAST.CustomPackets.PINGPONG;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import static org.koekepan.VAST.Packet.PacketWrapper.packetWrapperMap;

public class ClientSender implements Runnable{

    private PacketSender packetSender;
    private Session clientSession;
    public ClientSender(PacketSender packetSender, Session clientSession) {
        this.packetSender = packetSender;
        this.clientSession = clientSession;
    }
    private int queueNumberServerbound = 0;

    @Override
    public void run() {
        try {
            long timeAdded = System.currentTimeMillis();

            while (!packetSender.serverboundPacketQueue.isEmpty()) {
                boolean dosleep = true;
//                int queueNumberServerBoundLast = packetSender.queueNumberServerboundLast;
                boolean serverboundPacketQueueContainsKey = packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound);
                PacketWrapper wrapper = null;

//                System.out.println("ClientSender.run: packetSender.serverboundPacketQueue.size() = " + packetSender.serverboundPacketQueue.size() + " and queueNumberServerbound = " + queueNumberServerbound + " and serverboundPacketQueueContainsKey = " + serverboundPacketQueueContainsKey + " and serverboundPacketQueueContainsKey = " + serverboundPacketQueueContainsKey + " serverboundPacketQueuelast = " + queueNumberServerBoundLast);

                if (serverboundPacketQueueContainsKey) {
                    wrapper = PacketWrapper.getPacketWrapperByQueueNumber(packetSender.serverboundPacketQueue, queueNumberServerbound);

//                    System.out.println("ClientSender.run: wrapper = <" + wrapper.getPacket().getClass().getSimpleName() + "> and isProcessed = " + wrapper.isProcessed);

                    if (wrapper != null && wrapper.isProcessed && this.clientSession != null) {

//                        if (wrapper.getPacket().getClass().getSimpleName().equals("ClientPluginMessagePacket")) {
//                            System.out.println("ClientSender.run: <PLUGIN MESSAGE> (serverbound) Wrapper is: " + wrapper.getPacket().getClass().getSimpleName() + " and isProcessed: " + wrapper.isProcessed);
//                            System.out.println("With Channel: " + ((com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket)wrapper.getPacket()).getChannel());
//                        }

                        if (wrapper.getPacket() instanceof PINGPONG) {
                            PINGPONG pingpong = (PINGPONG) wrapper.getPacket();
                            if (pingpong.getType() == PINGPONG.Type.PING) {
                                PINGPONG pong = new PINGPONG(PINGPONG.Direction.CLIENTBOUND, PINGPONG.Origin.ServerProxy, PINGPONG.Type.PONG);
                                pong.setPingOriginServerID(pingpong.getPingOriginServerID()); // Very Important
                                pong.setInitTime(pingpong.getInitTime()); // Very Important

                                PacketWrapper packetWrapper = new PacketWrapper(pong);
                                packetWrapper.unique_id = wrapper.unique_id;
                                packetWrapper.clientBound = true;

//                                SPSPacket spsPacket = new SPSPacket(packet, emulatedClientConnection.getUsername(), 0,0, 0, emulatedClientConnection.getUsername());
//                                PacketWrapper.getPacketWrapper(packet).setSPSPacket(spsPacket);
//                                packetWrapper.setSPSPacket();

                                packetWrapperMap.put(pong, packetWrapper);

                                // LOG
//                                PacketCapture.log("PING_" + unique_id, PacketCapture.LogCategory.SERVERBOUND_PING_IN);


                                // PONGING!
//                                System.out.println("VastConnection.java => (INFO) Received PING packet, sending PONG packet");
                                this.packetSender.addClientBoundPacket(pong, packetWrapper.unique_id); // TODO: I think this should be below line below
                                this.packetSender.emulatedClientConnection.getPacketHandler().addPacket(packetWrapper);
                            }
                        }

                        if (!(wrapper.getPacket() instanceof PINGPONG)){
                            this.clientSession.send(wrapper.getPacket());
                        }
                        PacketCapture.log(this.packetSender.emulatedClientConnection.getUsername(),wrapper.getPacket().getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(wrapper.getPacket()), PacketCapture.LogCategory.SERVERBOUND_OUT);
//                        PacketCapture.log(wrapper.getPacket().getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(wrapper.getPacket()), PacketCapture.LogCategory.CLIENTBOUND_OUT);
//                        System.out.println("ClientSender.run: " + wrapper.getPacket().getClass().getSimpleName() + " sent to client: " + clientInstances_PacketSenders.get(this.packetSender).getUsername());

                        packetSender.removePacket(wrapper.getPacket());
                        timeAdded = System.currentTimeMillis(); // Reset time after sending a packet
                        queueNumberServerbound++;
                        dosleep = false;
                    }
                }

                // Handle timeout for both queues
                long currentTime = System.currentTimeMillis();
                if (currentTime - timeAdded > 1000) { // TODO: Change back to 100 when problem found (could be 50) - This if seems to break the system? (could be 20 for single client, multi client it should be more)
                    if (serverboundPacketQueueContainsKey) {
//                        if (!wrapper.getPacket().getClass().getSimpleName().equals("ServerChunkDataPacket")) {
                            System.out.println("ClientSender.run: <TIMED OUT> (serverbound) Wrapper is: " + wrapper.getPacket().getClass().getSimpleName() + " and isProcessed: " + wrapper.isProcessed);
                            PacketCapture.log(
                                    this.packetSender.emulatedClientConnection.getUsername(),
                                    wrapper.getPacket().getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(wrapper.getPacket()),
                                    PacketCapture.LogCategory.DELETED_PACKETS_TIME
                            );

                            packetSender.removePacket(wrapper.getPacket());
                            queueNumberServerbound++;
                            dosleep = false;
                            timeAdded = currentTime; // Reset time after handling timeouts
//                        }
                    }
                }

                // Check if the queue number has reached or exceeded the last queue number and increment until a packet is found
                if (!packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound) && queueNumberServerbound < packetSender.queueNumberServerboundLast) {
//                    while (!packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
                        queueNumberServerbound++;
                        dosleep = false;
                        timeAdded = currentTime; // Reset time after handling timeouts

//                        System.out.println("ClientSender.run: <INCREMENT> (clientbound) 1");
                        // Check if queueNumberServerbound has reached or exceeded the last queue number
//                        if (queueNumberServerbound > queueNumberServerBoundLast) {
////                            System.out.println("ClientSender.run: <BREAK> (clientbound) 1");
//                            break; // Exit the loop if we have reached the end of the queue
//                        }
//                    }
                }

                if (dosleep) {
                    Thread.sleep(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ClientSender.run: Exception: " + e.getMessage());
            // Log the exception
        }
    }
}
