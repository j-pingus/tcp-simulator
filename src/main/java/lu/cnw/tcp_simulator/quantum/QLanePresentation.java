package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QLanePresentation extends QuantumMessage{
    @JsonProperty("EvHeader")
    private QEventHeader event;

    @JsonProperty("HeatHeader")
    private QHeatHeader heat;

    @JsonProperty("Lane")
    private QLane lane;
}
