package lu.cnw.tcp_simulator;

import java.io.File;
import java.io.IOException;

public class MeetProcessor extends QuantumMessageProcessor {
    public MeetProcessor(String meet) throws IOException {
        super(
                new MultiFileInputStream(
                        FrameUtils.loadFrames(new File(meet + "/index.log")).stream()
                                .filter(f -> f.event == Event.FRAME)
                                .map(f -> new File(f.filename)).toList()
                )
        );
    }
}

