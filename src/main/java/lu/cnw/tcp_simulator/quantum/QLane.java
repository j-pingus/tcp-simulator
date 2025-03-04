package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class QLane {
    @JsonProperty("Ln")
    Integer ln;
    @JsonProperty("Rk")
    String rk;
    @JsonProperty("Name")
    String name;
    @JsonProperty("ScbName")
    String scbname;
    @JsonProperty("Nat")
    String nat;
    @JsonProperty("Club")
    String club;
    @JsonProperty("Hcp")
    String hcp;
    @JsonProperty("Cat")
    String cat;
    @JsonProperty("Born")
    String born;
    @JsonProperty("Time")
    String time;
    @JsonProperty("Stat")
    String stat;
    @JsonProperty("Note")
    String note;
    @JsonProperty("Swimmers")
    List<String> swimmers;
}
