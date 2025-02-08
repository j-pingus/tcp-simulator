package lu.cnw.tcp_simulator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Record {
    private static final String SERVER_IP = "127.0.0.1"; // Change this to the actual server IP
    private static final int SERVER_PORT = 9090;
    private static final String OUTPUT_DIR = "received_frames";
    private static final String INDEX_FILE = OUTPUT_DIR + "/index.log";

    public static void main(String[] args) {
        new File(OUTPUT_DIR).mkdirs(); // Ensure output directory exists

        while (true) {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 InputStream inputStream = socket.getInputStream()) {
                logEvent(System.currentTimeMillis(), Event.START,
                        "Connected to server " + SERVER_IP + ":" + SERVER_PORT);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    saveFrame(buffer, bytesRead);
                }
            } catch (IOException e) {
                logEvent(System.currentTimeMillis(), Event.DROP,
                        "Connection lost. Retrying in 1 second...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private static void saveFrame(byte[] data, int length) throws IOException {
        long timestamp = System.currentTimeMillis();
        String filename = OUTPUT_DIR + "/" + timestamp + ".bin";

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(data, 0, length);
        }
        logEvent(timestamp, Event.FRAME, filename);
    }

    private static void logEvent(long timestamp, Event event, String message) {
        System.out.println(message);
        logToFile(INDEX_FILE, timestamp + " - " + event + " - " + message);
    }

    private static void logToFile(String filename, String message) {
        try {
            Files.write(Path.of(filename), (message + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write to log: " + e.getMessage());
        }
    }
}
