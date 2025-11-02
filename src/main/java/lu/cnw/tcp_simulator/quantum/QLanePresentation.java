package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QLanePresentation extends QuantumMessage{
    @JsonProperty("EvHeader")
    private QEventHeader event;

    @JsonProperty("HeatHeader")
    private QHeatHeader heat;

    @JsonProperty("Lane")
    private QLane lane;
}
