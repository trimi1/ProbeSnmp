package snmp.handler;

import be.helmo.reseau.builder.Builder;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MulticastHandler implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(MulticastHandler.class);
    private final int multicastPort;
    private final String multicastAddress;
    private final String multicastInterface;
    private final int uniCastPort;
    private final String protocol;
    private final int multicastDelay;

    public MulticastHandler(String multicastAddress, int multicastDelay, int multicastPort, String multicastInterface, String protocol, int uniCastPort) {
        this.multicastAddress = multicastAddress;
        this.multicastDelay = multicastDelay;
        this.multicastPort = multicastPort;
        this.multicastInterface = multicastInterface;
        this.uniCastPort = uniCastPort;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                sendProbeMessage();
                Thread.sleep(this.multicastDelay * 1000L);
            } catch (IOException | InterruptedException e) {
                LOGGER.error("Error in MulticastHandler", e);
            }
        }
    }

    synchronized private void sendProbeMessage() throws IOException {
        String message = Builder.buildProbe(protocol, uniCastPort);
        LOGGER.info("Message PROBE has been sent");
        multicastSender(message);
    }

    public void sendDataMessage() throws IOException {
        String message = Builder.buildData(protocol, uniCastPort);
        multicastSender(message);
        LOGGER.info("MESSAGE DATA HAS BEEN SENT");
    }

    private void multicastSender(String message) throws IOException {
        InetAddress mcastaddr = InetAddress.getByName(multicastAddress);
        InetSocketAddress group = new InetSocketAddress(mcastaddr, multicastPort);
        NetworkInterface netIf = NetworkInterface.getByName(multicastInterface);
        try (MulticastSocket s = new MulticastSocket(multicastPort)) {
            s.setNetworkInterface(netIf);

            s.joinGroup(new InetSocketAddress(mcastaddr, 0), netIf);
            byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length, group);
            s.send(hi);
        }
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}