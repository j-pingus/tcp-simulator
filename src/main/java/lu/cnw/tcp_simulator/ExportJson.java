package lu.cnw.tcp_simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lu.cnw.tcp_simulator.quantum.QLane;
import lu.cnw.tcp_simulator.quantum.QuantumMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExportJson {
    private final ObjectMapper mapper = new ObjectMapper();
    private final NamePseudonymizer namePseudonymizer = new NamePseudonymizer("!!JavaSwimtime!!");

    public static void main(String[] args) throws IOException {
        var meet = "CIW-Session2";
        var processor = new MeetProcessor(meet);
        List<QuantumMessage> messages = new ArrayList<>();
        var exporter = new ExportJson();
        processor.process(message -> messages.add(exporter.anonymyse(message)));
        exporter.write(messages, new File(meet + ".json"));
    }

    public void write(Object o, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        mapper.writeValue(outputStream, o);
    }

    private void anonymyse(QLane lane) {
        if (lane != null & lane.getName() != null) {
            var relay = Optional.ofNullable(lane.getSwimmers()).orElse(new ArrayList<>()).size() > 1;
            var anonymysed = namePseudonymizer.pseudonymize(lane.getName(), "M".equals(lane.getCat()));
            var anonymysedClub = namePseudonymizer.pseudonymize(
                    lane.getClub().isEmpty() ? lane.getName() : lane.getClub(),
                    "M".equals(lane.getCat()));
            lane.setName(
                    relay ? anonymysedClub.club() : anonymysed.name()
            );
            lane.setScbname(anonymysed.name());
            lane.setClub(anonymysedClub.club());
            lane.setNat(anonymysedClub.club());
            lane.setBorn(anonymysed.birthdate());
            if (lane.getSwimmers() != null) {
                lane.setSwimmers(
                        lane.getSwimmers().stream().map(swimmer ->
                                namePseudonymizer.pseudonymize(swimmer, "M".equals(lane.getCat())).name()
                        ).toList()
                );
            }
        }

    }

    private QuantumMessage anonymyse(QuantumMessage message) {
        /*if (message instanceof QHeat qHeat) {
            qHeat.getLanes().forEach(this::anonymyse);
        }
        if(message instanceof QLanePresentation lanePresentation){
            anonymyse(lanePresentation.getLane());
        }*/
        return message;
    }
}
