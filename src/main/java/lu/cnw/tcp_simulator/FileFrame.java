package lu.cnw.tcp_simulator;

public class FileFrame {
        long timestamp;
        Event event;
        String filename;

        FileFrame(long timestamp,Event event, String filename) {
            this.timestamp = timestamp;
            this.filename = filename;
            this.event = event;
        }
    }
