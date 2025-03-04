package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QHeatHeader {
    @JsonProperty("Number")
    String number;
    @JsonProperty("Stat")
    String stat;

}
