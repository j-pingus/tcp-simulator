package lu.cnw.tcp_simulator.quantum;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class QEventHeader {
    @JsonProperty("CompName")
    String competitionName;
    @JsonProperty("Number")
    String number;
    @JsonProperty("Round")
    String round;
    @JsonProperty("Date")
    String date;
    @JsonProperty("Time")
    String time;
    @JsonProperty("Dist")
    String dist;
    @JsonProperty("Stroke")
    String stroke;
    @JsonProperty("Cat")
    String category;
    @JsonProperty("ScbTitle")
    String scoreboardTitle;
    @JsonProperty("Heats")
    Integer heats;
    @JsonProperty("Laps")
    Integer laps;
    @JsonProperty("Swimmers")
    Integer swimmers;
    @JsonProperty("Records")
    List<QRecord> records;

}
