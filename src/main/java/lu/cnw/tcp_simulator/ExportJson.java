package lu.cnw.tcp_simulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lu.cnw.tcp_simulator.quantum.QHeat;
import lu.cnw.tcp_simulator.quantum.QStart;
import lu.cnw.tcp_simulator.quantum.QuantumMessage;

import javax.naming.NameParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExportJson {
    public static void main(String[] args) throws IOException {
        var meet = "Minimeet-25m";
        var processor = new MeetProcessor(meet);
        List<QuantumMessage> messages = new ArrayList<>();
        var exporter = new ExportJson();
        processor.process(message->messages.add(exporter.anonymyse(message)));
        exporter.write(messages,new File(meet+".json"));
    }
    private final ObjectMapper mapper = new ObjectMapper();
    private final NamePseudonymizer namePseudonymizer = new NamePseudonymizer("!!JavaSwimtime!!");
    public void write(Object o, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        mapper.writeValue(outputStream, o);
    }
    private QuantumMessage anonymyse(QuantumMessage message) {
        if(message instanceof QHeat qHeat) {
            qHeat.getLanes().forEach(lane->{
                if(lane!=null &lane.getName()!=null) {
                    var anonymysed = namePseudonymizer.pseudonymize(lane.getName(), "M".equals(lane.getCat()));
                    var anonymysedClub = namePseudonymizer.pseudonymize(lane.getClub(), "M".equals(lane.getCat()));
                    lane.setName(anonymysed.name());
                    lane.setScbname(anonymysed.name());
                    lane.setClub(anonymysedClub.club());
                    lane.setNat(anonymysedClub.club());
                    lane.setBorn(anonymysed.birthdate());
                }
            });
        }
        return message;
    }
}
