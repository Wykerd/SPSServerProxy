package org.koekepan.VAST.Connection.PacketSenderRunnables;

import com.github.steveice10.packetlib.Session;
import org.koekepan.Performance.PacketCapture;
import org.koekepan.VAST.Connection.PacketSender;
import org.koekepan.VAST.Packet.PacketWrapper;
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
                int queueNumberServerBoundLast = packetSender.queueNumberServerboundLast;
                boolean serverboundPacketQueueContainsKey = packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound);
                PacketWrapper wrapper = null;

//                System.out.println("ClientSender.run: packetSender.serverboundPacketQueue.size() = " + packetSender.serverboundPacketQueue.size() + " and queueNumberServerbound = " + queueNumberServerbound + " and serverboundPacketQueueContainsKey = " + serverboundPacketQueueContainsKey + " and serverboundPacketQueueContainsKey = " + serverboundPacketQueueContainsKey + " serverboundPacketQueuelast = " + queueNumberServerBoundLast);

                if (serverboundPacketQueueContainsKey) {
                    wrapper = PacketWrapper.getPacketWrapperByQueueNumber(packetSender.serverboundPacketQueue, queueNumberServerbound);

//                    System.out.println("ClientSender.run: wrapper = <" + wrapper.getPacket().getClass().getSimpleName() + "> and isProcessed = " + wrapper.isProcessed);

                    if (wrapper != null && wrapper.isProcessed && this.clientSession != null) {

                        PacketCapture.log(wrapper.getPacket().getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(wrapper.getPacket()), PacketCapture.LogCategory.SERVERBOUND_OUT);
                        this.clientSession.send(wrapper.getPacket());
//                        PacketCapture.log(wrapper.getPacket().getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(wrapper.getPacket()), PacketCapture.LogCategory.CLIENTBOUND_OUT);
//                        System.out.println("ClientSender.run: " + wrapper.getPacket().getClass().getSimpleName() + " sent to client: " + clientInstances_PacketSenders.get(this.packetSender).getUsername());

                        packetSender.removePacket(wrapper.getPacket());
                        timeAdded = System.currentTimeMillis(); // Reset time after sending a packet
                        queueNumberServerbound++;
                    }
                }

                // Handle timeout for both queues
                long currentTime = System.currentTimeMillis();
                if (currentTime - timeAdded > 50) { // TODO: Change back to 100 when problem found (could be 50) - This if seems to break the system? (could be 20 for single client, multi client it should be more)
                    if (serverboundPacketQueueContainsKey) {
//                        System.out.println("PacketSender.run: <TIMED OUT> (clientbound) 1");
//                        if (!wrapper.getPacket().getClass().getSimpleName().equals("ServerChunkDataPacket")) {
                            System.out.println("ClientSender.run: <TIMED OUT> (serverbound) Wrapper is: " + wrapper.getPacket().getClass().getSimpleName() + " and isProcessed: " + wrapper.isProcessed);
                            packetSender.removePacket(wrapper.getPacket());
                            queueNumberServerbound++;
                            timeAdded = currentTime; // Reset time after handling timeouts
//                        }
                    }
                }

                // Check if the queue number has reached or exceeded the last queue number and increment until a packet is found
                if (!packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound) && queueNumberServerbound <= queueNumberServerBoundLast) {
                    while (!packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
                        queueNumberServerbound++;
                        timeAdded = currentTime; // Reset time after handling timeouts

//                        System.out.println("ClientSender.run: <INCREMENT> (clientbound) 1");
                        // Check if queueNumberServerbound has reached or exceeded the last queue number
                        if (queueNumberServerbound > queueNumberServerBoundLast) {
//                            System.out.println("ClientSender.run: <BREAK> (clientbound) 1");
                            break; // Exit the loop if we have reached the end of the queue
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ClientSender.run: Exception: " + e.getMessage());
            // Log the exception
        }
    }
}
