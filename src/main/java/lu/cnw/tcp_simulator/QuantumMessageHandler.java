package lu.cnw.tcp_simulator;

import lu.cnw.tcp_simulator.quantum.QuantumMessage;

@FunctionalInterface
interface QuantumMessageHandler {
    void handle(QuantumMessage message);
}
