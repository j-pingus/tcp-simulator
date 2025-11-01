package lu.cnw.tcp_simulator;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
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
        try {
            var quantumMessage = mapper.readValue(frame, QuantumMessage.class);
            return Optional.of(quantumMessage);
        } catch (JacksonException e) {

            System.out.println("Could not parse: " + e.getMessage());
            System.out.println("json: " + frame);
            throw e;
        }
    }

    public void process(QuantumMessageHandler handler) throws IOException {
        var message = provide();
        while (message.isPresent()) {
            handler.handle(message.get());
            message = provide();
        }
    }

}
