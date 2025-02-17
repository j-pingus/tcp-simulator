package lu.cnw.tcp_simulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FrameUtils {
    public static List<FileFrame> loadFrames(File indexFile) {
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
