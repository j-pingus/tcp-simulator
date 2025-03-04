package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QRt extends QuantumMessage{
    @JsonProperty("RT")
    String rt;
}
