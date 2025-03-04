package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QPoolTest extends QuantumMessage {
    @JsonProperty("Contact")
    String contact;
    @JsonProperty("Side")
    String side;
    @JsonProperty("Ln")
    String ln;
    @JsonProperty("Total")
    String total;
}
