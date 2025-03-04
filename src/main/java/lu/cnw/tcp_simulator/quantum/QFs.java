package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QFs extends QuantumMessage {
    @JsonProperty("Fr")
    String fr;
}
