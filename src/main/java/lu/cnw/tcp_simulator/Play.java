package lu.cnw.tcp_simulator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Play {
    private static final int SERVER_PORT = 12345;
    private static final String INPUT_DIR = "received_frames";
    private final int serverPort;
    private final File inputDir;
    private final File indexFile;

    public Play(int serverPort, String inputDir) {
        this.serverPort = serverPort;
        this.inputDir = new File(inputDir);
        this.indexFile = new File(inputDir, "index.log");
        if (!this.indexFile.exists()) {
            throw new Error("Index file not found:" + indexFile);
        }
    }

    public static void main(String[] args) {
        Play play = new Play(SERVER_PORT, INPUT_DIR);
        List<FileFrame> frames = play.loadFrames();

        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                System.out.println("Replay server started on port " + SERVER_PORT);

                while (true) {
                    try (Socket socket = serverSocket.accept();
                         OutputStream outputStream = socket.getOutputStream()) {
                        System.out.println("Client connected: " + socket.getInetAddress());
                        replayFrames(outputStream, frames);
                    } catch (IOException e) {
                        System.out.println("Connection lost. Waiting for a new client...");
                    }
                }
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    private static void replayFrames(OutputStream outputStream, List<FileFrame> frames) throws IOException {
        if (frames.isEmpty()) return;
        long startTime = System.currentTimeMillis();
        long firstFrameTime = frames.get(0).timestamp;

        for (int i = 0; i < frames.size(); i++) {
            FileFrame frame = frames.get(i);
            byte[] data = Files.readAllBytes(Path.of(frame.filename));

            long delay = (frame.timestamp - firstFrameTime) - (System.currentTimeMillis() - startTime);
            if (delay > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException ignored) {
                }
            }

            outputStream.write(data);
            outputStream.flush();

            if (Math.random() < 0.1) { // Simulate random connection drops
                System.out.println("Simulating connection drop.");
                break;
            }
        }
    }

    private List<FileFrame> loadFrames() {
        List<FileFrame> frames = new ArrayList<>();
        try (Scanner scanner = new Scanner(indexFile)) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(" - ");
                if (parts.length == 3) {
                    long timestamp = Long.parseLong(parts[0]);
                    String filename = parts[2];
                    Event event = Event.valueOf(parts[1]);
                    frames.add(new FileFrame(timestamp, event, filename));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load index file: " + e.getMessage());
        }
        return frames;
    }

}
