package lu.cnw.tcp_simulator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Record {
    private static final String SERVER_IP = "quantum"; // Change this to the actual server IP
    private static final int SERVER_PORT = 26700;
    private static final String OUTPUT_DIR = "received_frames";
    private final File outputDir;
    private final int serverPort;
    private final String serverIp; // Change this to the actual server IP
    private final URI indexFile;

    public Record(String serverIp, int serverPort, String outputDir) {
        this.outputDir = new File(outputDir);
        this.outputDir.mkdirs(); // Ensure output directory exists
        this.serverPort = serverPort;
        this.serverIp = serverIp;
        this.indexFile = new File(this.outputDir, "index.log").toURI();
    }

    public static void main(String[] args) {
        Record record;
        if (args.length != 3) {
            record = new Record(SERVER_IP, SERVER_PORT, OUTPUT_DIR);
        } else {
            record = new Record(args[0], Integer.parseInt(args[1]), args[2]);
        }
        record.record();
    }

    private void logToFile(String message) {
        try {
            Files.write(Path.of(this.indexFile), (message + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write to log: " + e.getMessage());
        }
    }

    private void saveFrame(byte[] data, int length) throws IOException {
        long timestamp = System.currentTimeMillis();
        String filename = this.outputDir.toString() + "/" + timestamp + ".bin";

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(data, 0, length);
        }
        logEvent(timestamp, Event.FRAME, filename);
    }

    private void logEvent(long timestamp, Event event, String message) {
        System.out.println(message);
        logToFile(timestamp + " - " + event + " - " + message);
    }

    public void record() {
        while (true) {
            try (Socket socket = new Socket(serverIp, serverPort);
                 InputStream inputStream = socket.getInputStream()) {
                logEvent(System.currentTimeMillis(), Event.START,
                        "Connected to server " + serverIp + ":" + serverPort);

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
}
