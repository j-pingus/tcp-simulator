package lu.cnw.tcp_simulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class View {
    private static final String INPUT_DIR = "Minimeet";
    private final File indexFile;

    public View(String inputDir) {
        this.indexFile = new File(inputDir, "index.log");
        if (!this.indexFile.exists()) {
            throw new Error("Index file not found:" + indexFile);
        }
    }

    public static void main(String[] args)throws Exception {
        View play;
        if (args.length != 2) {
            play = new View(INPUT_DIR);
        } else {
            play = new View(args[0]);
        }
        List<FileFrame> frames = FrameUtils.loadFrames(play.indexFile);
        play.view(frames);

    }

    public static String convertTimestampToReadable(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); // Define the format with milliseconds
        Date date = new Date(timestamp); // Create a Date object from the timestamp
        return sdf.format(date); // Format the date into a readable string
    }

    public static String dumpByteArray(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            if (b >= 32 && b <= 126) { // Printable ASCII range
                sb.append((char) b);
            } else {
                sb.append(String.format("\\x%02X", b)); // Convert to hex format
            }
        }
        return sb.toString();
    }

    private void view(List<FileFrame> frames) throws IOException {
        if (frames.isEmpty()) return;
        long previousTimestamp = frames.getFirst().timestamp;
        for (FileFrame frame : frames) {
            long delay = frame.timestamp - previousTimestamp;
            String message = frame.filename;
            if (frame.event == Event.FRAME) {
                byte[] data = Files.readAllBytes(Path.of(frame.filename));

                previousTimestamp = frame.timestamp;
                message = dumpByteArray(data);
            }
            System.out.printf("%s-%06d(%5s):%s%n",
                    convertTimestampToReadable(frame.timestamp),
                    delay,
                    frame.event,
                    message);
        }
    }

}
