package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QSt extends QuantumMessage {
    @JsonProperty("TS")
    String timestamp;
    @JsonProperty("Type")
    String type;
}
