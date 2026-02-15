package lu.cnw.tcp_simulator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AlgueRecord implements Runnable {
    private final int port;
    private final List<MeetAction> actions = new ArrayList<>();
    private volatile boolean running = true;

    public AlgueRecord(int port) {
        this.port = port;
    }

    public void addAction(MeetAction action) {
        if (action != null) actions.add(action);
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            socket.setSoTimeout(1000);
            byte[] buf = new byte[2048];
            while (running) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                    // extract raw payload string (tabs preserved) and also a parsed map for compatibility
                    String raw = payloadToString(packet.getData(), packet.getLength());
                    Map<String, String> map = parsePayload(packet.getData(), packet.getLength());
                    for (MeetAction action : actions) {
                        try {
                            action.handle(map, packet, raw);
                        } catch (Exception e) {
                            System.err.println("MeetAction failed: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    // timeout or socket closed — ignore and loop if still running
                    if (!running) break;
                }
            }
        } catch (IOException e) {
            System.err.println("UDPMeetListener error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String payloadToString(byte[] data, int length) {
        String s;
        try {
            s = new String(data, 0, length, StandardCharsets.US_ASCII);
        } catch (Exception e) {
            s = new String(data, 0, length);
        }
        // Trim only trailing CR/LF characters but preserve internal tabs
        s = s.replaceAll("[\r\n]+$", "");
        return s;
    }

    public static Map<String, String> parsePayload(byte[] data, int length) {
        String s = payloadToString(data, length);
        String[] tokens = s.split("\t");
        Map<String, String> map = new HashMap<>();
        if (tokens.length >= 3 && tokens.length % 3 == 0) {
            for (int i = 0; i < tokens.length; i += 3) {
                String key = tokens[i].trim();
                String sub = tokens[i + 1].trim();
                String val = tokens[i + 2].trim();
                if (!key.isEmpty() && !sub.isEmpty()) {
                    map.put(key + "." + sub, val);
                } else if (!key.isEmpty()) {
                    map.put(key, val);
                }
            }
        } else {
            // fallback pairwise
            for (int i = 0; i + 1 < tokens.length; i += 2) {
                String key = tokens[i].trim();
                String val = tokens[i + 1].trim();
                if (!key.isEmpty()) map.put(key, val);
            }
        }
        return map;
    }

    public static class FileLogAction implements MeetAction {
        private final Path logFile;
        private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS")
                .withLocale(Locale.US).withZone(ZoneId.systemDefault());

        public FileLogAction(Path logFile) {
            this.logFile = logFile;
            try {
                Files.createDirectories(logFile.getParent());
            } catch (IOException ignored) {
            }
        }

        @Override
        public void handle(Map<String, String> payload, DatagramPacket packet, String rawPayload) {
            StringBuilder line = new StringBuilder();
            String ts = fmt.format(Instant.now());
            InetAddress addr = packet.getAddress();
            int port = packet.getPort();
            line.append(ts).append(" ").append(addr.getHostAddress()).append(":").append(port).append(" ");
            // Append the original payload with tabs preserved
            line.append(rawPayload);
            line.append(System.lineSeparator());
            try {
                Files.write(logFile, line.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println("Failed to write meet log: " + e.getMessage());
            }
        }
    }

    public interface MeetAction {
        void handle(Map<String, String> payload, DatagramPacket origin, String rawPayload);
    }

    public static void main(String[] args) {
        int port = 26;
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        AlgueRecord listener = new AlgueRecord(port);
        Path log = Path.of("received_frames", "meet.log");
        listener.addAction(new FileLogAction(log));
        listener.addAction((payload, packet, raw) -> {
            System.out.println("Received from " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
            // Print tokens for quick inspection but do not assume any particular field names
            String[] tokens = raw.split("\t");
            for (int i = 0; i < tokens.length; i++) {
                System.out.println("  [" + i + "] " + tokens[i]);
            }
        });
        System.out.println("Listening for Meet UDP packets on port " + port + " (press CTRL+C to stop)");
        listener.run();
    }
}
