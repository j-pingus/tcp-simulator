package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QTime extends QuantumMessage {
    @JsonProperty("Rk")
    String rk;
    @JsonProperty("Ln")
    Integer ln;
    @JsonProperty("Time")
    String time;
    @JsonProperty("Kind")
    String kind;
    @JsonProperty("Type")
    String type;
    @JsonProperty("Lap")
    Integer lap;
    @JsonProperty("Stat")
    String stat;
    @JsonProperty("Dist")
    String dist;
    @JsonProperty("LT")
    String lt;
    @JsonProperty("CurrentRelaySwimmer")
    String currentRelaySwimmer;
    @JsonProperty("EventNumber")
    String eventNumber;
    @JsonProperty("HeatNumber")
    String heatNumber;
    @JsonProperty("HeadID")
    Long headID;
}
