package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QStart extends QuantumMessage {
    @JsonProperty("TS")
    String timestamp;
    @JsonProperty("Type")
    String type;
}
