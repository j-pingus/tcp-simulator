package lu.cnw.tcp_simulator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Play {
    private static final int SERVER_PORT = 12345;
    private static final String INPUT_DIR = "received_frames";
    private final int serverPort;
    private final File inputDir;
    private final boolean stepper;

    public Play(int serverPort, String inputDirName, boolean stepper) {
        this.serverPort = serverPort;
        this.inputDir = new File(inputDirName).getAbsoluteFile();
        var indexFile = new File(this.inputDir, "index.log");
        this.stepper = stepper;
        if (!indexFile.exists()) {
            throw new Error("Index file not found:" + indexFile);
        }
    }

    public static void main(String[] args) {
        Play play;
        boolean stepper = args.length >= 3 && args[2].equals("stepper");
        if (args.length < 2) {
            play = new Play(SERVER_PORT, INPUT_DIR, stepper);
        } else {
            play = new Play(Integer.parseInt(args[0]), args[1], stepper);
        }
        List<FileFrame> frames = FrameUtils.loadFrames(new File(play.inputDir, "index.log"));
        play.play(frames);
    }

    private static void replayFrames(OutputStream outputStream, File inputDir, List<FileFrame> frames, DelayConsumer consumer) throws IOException {
        if (frames.isEmpty()) return;
        long startTime = System.currentTimeMillis();
        long firstFrameTime = frames.getFirst().timestamp;

        for (FileFrame frame : frames) {
            if (frame.event == Event.FRAME) {
                byte[] data = Files.readAllBytes(Path.of(inputDir.getParentFile().getAbsolutePath()).resolve(frame.filename));
                long delay = (frame.timestamp - firstFrameTime) - (System.currentTimeMillis() - startTime);
                consumer.consume(delay);
                outputStream.write(data);
                outputStream.flush();
                System.out.println("Frame replayed: " + frame.filename);
            }
            //TODO: implement the drop"
        }
    }

    public void play(List<FileFrame> frames) {

        try (ServerSocket serverSocket = new ServerSocket(this.serverPort)) {
            System.out.println("Replay server started on port " + this.serverPort);
            var scanner = new Scanner(System.in);
            while (true) {
                try (Socket socket = serverSocket.accept();
                     OutputStream outputStream = socket.getOutputStream()) {
                    System.out.println("Client connected: " + socket.getInetAddress());
                    if (stepper) {
                        System.out.println("Type <ENTER> to send next packet");
                    }
                    replayFrames(outputStream, this.inputDir, frames, stepper ?
                            (delay) -> scanner.nextLine() :
                            (delay) -> {
                                if (delay > 0) {
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(delay);
                                    } catch (InterruptedException ignored) {
                                    }
                                }
                            });
                } catch (IOException e) {
                    System.out.println("Connection lost. Waiting for a new client...");
                    e.printStackTrace();
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

    @FunctionalInterface
    interface DelayConsumer {
        void consume(long delay);
    }
}