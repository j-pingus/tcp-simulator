package lu.cnw.tcp_simulator;

import lu.cnw.tcp_simulator.quantum.QHeatOfficial;
import lu.cnw.tcp_simulator.quantum.QSl;
import lu.cnw.tcp_simulator.quantum.QTime;
import lu.cnw.tcp_simulator.quantum.QuantumMessage;

import java.io.IOException;

public class TestFramedInputStream {
    public static void main(String[] args) throws IOException {
        var meet = "CIW-Session2";
        var processor = new MeetProcessor(meet);
        processor.process(TestFramedInputStream::testMessage);
    }

    private static void testMessage(QuantumMessage message) {
        switch (message) {

            case QHeatOfficial qho ->
                    System.out.println("\nho:" + qho.getEvent().getNumber() + ":" + qho.getHeat().getNumber());
            case QSl qsl -> System.out.println("sl:" + qsl.getEvent().getNumber() + ":" + qsl.getHeat().getNumber());
            case QTime qTime ->
                    System.out.print("\tqt:" + qTime.getLn() + "/" + qTime.getType() + ":" + qTime.getTime());
            default -> {
            }
        }
    }
}
