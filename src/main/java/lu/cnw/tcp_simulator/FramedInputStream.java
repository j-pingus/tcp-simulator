package lu.cnw.tcp_simulator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FramedInputStream extends InputStream {
    private final InputStream inputStream;
    private final byte startFrame;
    private final byte endFrame;
    private boolean closed = false;

    public FramedInputStream(InputStream inputStream) {
        this(inputStream, (byte) 0x01, (byte) 0x04);
    }

    public FramedInputStream(InputStream inputStream, byte startFrame, byte endFrame) {
        this.inputStream = inputStream;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
    }

    /**
     * Reads a full frame from the stream.
     * If a new start byte (0x01) is encountered while reading, it restarts the frame.
     *
     * @return A byte array containing the frame's payload (excluding 0x01 and 0x04), or null if no more frames.
     * @throws IOException if an error occurs during reading.
     */
    public String readFrame() throws IOException {
        if (closed) return null;
        ArrayList<Byte> buffer = new ArrayList<>();
        boolean inFrame = false;
        int byteRead;

        while ((byteRead = inputStream.read()) != -1) {
            if (byteRead == startFrame) {
                // Found start byte: Discard buffer and start a new frame
                if (!buffer.isEmpty()) {
                    System.err.println("DISCARDED-" + toString(buffer));
                }
                buffer.clear();
                inFrame = true;
                continue;
            }

            if (inFrame) {
                if (byteRead == endFrame) {
                    // Found end byte: Return the frame data
                    return toString(buffer);
                } else {
                    buffer.add((byte) byteRead);
                }
            } else {
                System.err.println("DISCARDED-" + (char) byteRead);
            }
        }
        if (!buffer.isEmpty()) {
            System.err.println("DISCARDED-" + toString(buffer));
        }
        return null; // No complete frame found
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        closed = true;
    }

    private String toString(ArrayList<Byte> buffer) {
        byte[] frameData = new byte[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            frameData[i] = buffer.get(i);
        }
        return new String(frameData);
    }
}
