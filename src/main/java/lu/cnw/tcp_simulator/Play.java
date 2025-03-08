package lu.cnw.tcp_simulator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Play {
    private static final int SERVER_PORT = 12345;
    private static final String INPUT_DIR = "received_frames";
    private final int serverPort;
    private final File indexFile;

    public Play(int serverPort, String inputDir) {
        this.serverPort = serverPort;
        this.indexFile = new File(inputDir, "index.log");
        if (!this.indexFile.exists()) {
            throw new Error("Index file not found:" + indexFile);
        }
    }

    public static void main(String[] args) {
        Play play;
        if (args.length != 2) {
            play = new Play(SERVER_PORT, INPUT_DIR);
        } else {
            play = new Play(Integer.parseInt(args[0]), args[1]);
        }
        List<FileFrame> frames = FrameUtils.loadFrames(play.indexFile);
        play.play(frames);
    }

    private static void replayFrames(OutputStream outputStream, List<FileFrame> frames) throws IOException {
        if (frames.isEmpty()) return;
        long startTime = System.currentTimeMillis();
        long firstFrameTime = frames.getFirst().timestamp;

        for (FileFrame frame : frames) {
            if (frame.event == Event.FRAME) {
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
            }
            //TODO: implement the drop"
        }
    }

    public void play(List<FileFrame> frames) {
        try (ServerSocket serverSocket = new ServerSocket(this.serverPort)) {
            System.out.println("Replay server started on port " + this.serverPort);

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
