package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QResetTimer extends QuantumMessage {
    @JsonProperty("RT")
    String rt;
}
