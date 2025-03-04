package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class QHeat extends QuantumMessage{
    @JsonProperty("EvHeader")
    private QEventHeader event;

    @JsonProperty("HeatHeader")
    private QHeatHeader heat;

    @JsonProperty("Lanes")
    private List<QLane> lanes;

}
