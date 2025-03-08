package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "Msg")
@JsonSubTypes({
        @JsonSubTypes.Type(value = QTime.class, name = "TK"),
        @JsonSubTypes.Type(value = QFs.class, name = "FS"),
        @JsonSubTypes.Type(value = QSl.class, name = "SL"),
        @JsonSubTypes.Type(value = QResetTimer.class, name = "RT"),
        @JsonSubTypes.Type(value = QStart.class, name = "ST"),
        @JsonSubTypes.Type(value = QPoolTest.class, name = "POOLTEST"),
        @JsonSubTypes.Type(value = QHeatOfficial.class, name = "HO")
})
public class QuantumMessage {
    @JsonProperty("Msg")
    public String type;
}
