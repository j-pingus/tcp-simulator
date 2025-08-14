package lu.cnw.tcp_simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lu.cnw.tcp_simulator.quantum.QuantumMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;


public class QuantumMessageProcessor {
    private final FramedInputStream framedInputStream;
    private final ObjectMapper mapper = new ObjectMapper();

    public QuantumMessageProcessor(InputStream sitbInputStream) {
        this.framedInputStream = new FramedInputStream(sitbInputStream);
    }

    private Optional<QuantumMessage> provide() throws IOException {
        var frame = framedInputStream.readFrame();
        while (frame != null && frame.isEmpty()) {
            frame = framedInputStream.readFrame();
        }
        if (frame == null) {
            framedInputStream.close();
            return Optional.empty();
        }
        var quantumMessage = mapper.readValue(frame, QuantumMessage.class);
        return Optional.of(quantumMessage);
    }

    public void process(QuantumMessageHandler handler) throws IOException {
        var message = provide();
        while (message.isPresent()) {
            handler.handle(message.get());
            message = provide();
        }
    }

}
