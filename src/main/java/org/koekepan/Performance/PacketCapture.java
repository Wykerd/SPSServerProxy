package org.koekepan.Performance;

import org.koekepan.App;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PacketCapture {

    // Enum to define log categories
    public enum LogCategory {
        SERVERBOUND_IN,
        SERVERBOUND_OUT,
        CLIENTBOUND_IN,
        CLIENTBOUND_OUT,
        DELETED_PACKETS_TIME,
        DELETED_PACKETS,
        INIT_SEND,
        ADD_TO_OUTGOING_QUEUE,
        PROCESSING_START,
        UNKNOWN,

//        CLIENTBOUND_PING_IN,
//        CLIENTBOUND_PING_OUT,
//        CLIENTBOUND_PONG_IN,
        CLIENTBOUND_PONG_OUT,

        SERVERBOUND_PING_IN,
//        SERVERBOUND_PING_OUT,
//        SERVERBOUND_PONG_IN,
//        SERVERBOUND_PONG_OUT
    }

    // Directory where logs will be stored
    private static final String LOG_DIR = "./packet_results/";

    // Set to keep track of files that have already been prepared
    private static final Set<String> preparedFiles = new HashSet<>();

    private static void prepareFile(String filename) throws IOException {
        synchronized (preparedFiles) {
            if (preparedFiles.contains(filename)) {
                return;
            }

            File file = new File(filename);
            File parentDir = file.getParentFile();

            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (file.exists()) {
                new FileWriter(filename, false).close(); // This will empty the file
            }

            preparedFiles.add(filename);
        }
    }

    // Synchronized to make it thread-safe
    private static synchronized void logs(String message, LogCategory category) {
        FileWriter fileWriter = null;
        String targetFilename = getFilenameByCategory(category);

        try {
            prepareFile(targetFilename);

            // Append to the existing file or create a new one if it doesn't exist
            fileWriter = new FileWriter(targetFilename, true);

            // Create a SimpleDateFormat object to format date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            // Get the current date and time
            String currentTime = dateFormat.format(new Date());

            // Write the date, time, and message to the file
            fileWriter.append(currentTime);
            fileWriter.append(",");
            fileWriter.append(message);
            fileWriter.append("\n");

        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
        } finally {
            try {
                // Close the FileWriter
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                // Handle exceptions
                e.printStackTrace();
            }
        }
    }

    public static synchronized void log(String Username, String message, LogCategory category) {
//        message = message + ", "  + Username + ", " +  App.config.getLogHostName(); // Username is not NULL
        message = message + ", "  + Username + ", " +  App.config.getLogHostName(); // Username is not NULL
        log(message, category);
    }

    public static synchronized void log(String message, LogCategory category) {
        message = message + ", NULL , " + App.config.getLogHostName(); // Username is NULL
        logs(message, category);
    }

    // Helper method to get filename based on log category
    private static String getFilenameByCategory(LogCategory category) {
        String filename = "";
        switch (category) {
            case SERVERBOUND_IN:
                filename = "sp_serverbound_in_packet_log.csv";
                break;
            case SERVERBOUND_OUT:
                filename = "sp_serverbound_out_packet_log.csv";
                break;
            case CLIENTBOUND_IN:
                filename = "sp_clientbound_in_packet_log.csv";
                break;
            case CLIENTBOUND_OUT:
                filename = "sp_clientbound_out_packet_log.csv";
                break;
            case ADD_TO_OUTGOING_QUEUE:
                filename = "sp_add_to_outgoing_queue_packet_log.csv";
                break;
            case INIT_SEND:
                filename = "sp_init_send_packet_log.csv";
                break;
            case DELETED_PACKETS:
                filename = "sp_deleted_packets.csv";
                break;
            case DELETED_PACKETS_TIME:
                filename = "sp_deleted_packets_time.csv";
                break;
            case PROCESSING_START:
                filename = "sp_processing_START_packet_log.csv";
                break;
            case UNKNOWN:
                filename = "sp_clientbound_in_unknownRecipient_packet_log.csv";
                break;
            case CLIENTBOUND_PONG_OUT:
                filename = "sp_clientbound_pong_out_packet_log.csv";
                break;
            case SERVERBOUND_PING_IN:
                filename = "sp_serverbound_ping_in_packet_log.csv";
                break;

        }
        return LOG_DIR + filename;
    }
}
