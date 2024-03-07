package thread;

import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IBuilder;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;


public class MulticastSender implements Runnable{
    private int multicastPort;
    private String multicastAdress;
    private String multicastInterface;
    private int uniCastPort;
    private String protocol;
    private int multicastDelay;

    public MulticastSender(String multicastAddress, int multicastDelay, int multicastPort, String multicastInterface, String protocol, int uniCastPort) {
        this.multicastAdress = multicastAddress;
        this.multicastDelay = multicastDelay;
        this.multicastPort = multicastPort;
        this.multicastInterface = multicastInterface;
        this.uniCastPort = uniCastPort;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sendProbeMessage();
                Thread.sleep(multicastDelay*1000L);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //synchronized private void sendProbeMessage(String Protocol, int port) throws IOException {
    synchronized private void sendProbeMessage() throws IOException {
        IBuilder builder = new Builder();
        String message = builder.buildProbe(protocol, uniCastPort);
        InetAddress mcastaddr = InetAddress.getByName(multicastAdress);
        InetSocketAddress group = new InetSocketAddress(mcastaddr, multicastPort);
        NetworkInterface netIf = NetworkInterface.getByName(multicastInterface);
        System.out.printf("Interface: %s\n", multicastInterface);
        MulticastSocket s = new MulticastSocket(multicastPort);
        s.setNetworkInterface(netIf);

        s.joinGroup(new InetSocketAddress(mcastaddr, 0), netIf);
        byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length, group);
        s.send(hi);
    }
    //public void sendDataMessage(String Protocol, int port) throws IOException {
    public void sendDataMessage() throws IOException {
        IBuilder builder = new Builder();
        String message = builder.buildData(protocol, uniCastPort);
        InetAddress mcastaddr = InetAddress.getByName(multicastAdress);
        InetSocketAddress group = new InetSocketAddress(mcastaddr, multicastPort);
        NetworkInterface netIf = NetworkInterface.getByName(multicastInterface);
        MulticastSocket s = new MulticastSocket(multicastPort);

        s.joinGroup(new InetSocketAddress(mcastaddr, 0), netIf);
        byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length, group);
        s.send(hi);
    }
    public void startThread() {
        (new Thread(this)).start();
    }
}
