package lu.cnw.tcp_simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lu.cnw.tcp_simulator.quantum.QHeatOfficial;
import lu.cnw.tcp_simulator.quantum.QSl;
import lu.cnw.tcp_simulator.quantum.QTime;
import lu.cnw.tcp_simulator.quantum.QuantumMessage;

import java.io.File;
import java.io.IOException;

public class TestFramedInputStream {
    public static void main(String[] args) throws IOException {
        var frames = FrameUtils.loadFrames(new File("Minimeet/index.log"));
        var inputStream = new FramedInputStream(
                new MultiFileInputStream(
                        frames.stream()
                                .filter(f -> f.event == Event.FRAME)
                                .map(f -> new File(f.filename)).toList()
                )
        );
        String frame;
        ObjectMapper mapper = new ObjectMapper();
        while ((frame = inputStream.readFrame()) != null) {
            //Reading json as tree, just to confirm it is well formed
            var tree = mapper.readTree(frame);
            if (!frame.isEmpty()) {
                var message = mapper.readValue(frame, QuantumMessage.class);
                if (message instanceof QHeatOfficial qho) {
                    System.out.println("official:" + qho.getEvent().getNumber() + ":" + qho.getHeat().getNumber());
                }
                if (message instanceof QSl qsl) {
                    System.out.println("sl:" + qsl.getEvent().getNumber() + ":" + qsl.getHeat().getNumber());
                }
                if (message instanceof QTime qTime && qTime.getType()!=null) {
                    System.out.println(qTime.getLn() + "/" + qTime.getType() + ":" + qTime.getTime());
                }
            }
        }
    }
}
