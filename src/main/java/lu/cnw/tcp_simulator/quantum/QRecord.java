package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class QRecord {

    @JsonProperty("Name")
    String name;
    @JsonProperty("Time")
    String time;
    @JsonProperty("Splits")
    List<String> splits;
}
