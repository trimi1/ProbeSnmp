package thread;

import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class MulticastSender implements Runnable{

    private Thread probeThread;
    private int multicastPort;
    private String multicastAdress;
    private String multicastInterface;
    private int uniCastPort;
    private String protocol;
    public MulticastSender(String multicastAddress, int multicastPort, String multicastInterface, String protocol, int uniCastPort) {
        this.multicastAdress = multicastAddress;
        this.multicastPort = multicastPort;
        this.multicastInterface = multicastInterface;
        this.uniCastPort = uniCastPort;
        this.protocol = protocol;
        startThread();
    }
    public void run() {
        try {
            sendProbeMessage(protocol, uniCastPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized private void sendProbeMessage(String Protocol, int port) throws IOException {
        /*IBuilder builder = new Builder();
        String message = builder.buildProbe(Protocol, port);
        byte[] bytes = message.getBytes();
        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(multicastAdress);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, multicastPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        IBuilder builder = new Builder();
        String message = builder.buildProbe(Protocol, port);
        InetAddress mcastaddr = InetAddress.getByName(multicastAdress);
        InetSocketAddress group = new InetSocketAddress(mcastaddr, multicastPort);
        NetworkInterface netIf = NetworkInterface.getByName(multicastInterface);
        MulticastSocket s = new MulticastSocket(multicastPort);

        s.joinGroup(new InetSocketAddress(mcastaddr, 0), netIf);
        byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length, group);
        s.send(hi);
    }

    public void sendDataMessage(String Protocol, int port) throws IOException {
        /*IBuilder builder = new Builder();
        String message = builder.buildProbe(Protocol, port);
        byte[] bytes = message.getBytes();
        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(multicastAdress);
            NetworkInterface networkInterface = NetworkInterface.getByName("ethernet_32776");
            socket.setNetworkInterface(networkInterface);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, multicastPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        IBuilder builder = new Builder();
        String message = builder.buildProbe(Protocol, port);
        InetAddress mcastaddr = InetAddress.getByName(multicastAdress);
        InetSocketAddress group = new InetSocketAddress(mcastaddr, port);
        NetworkInterface netIf = NetworkInterface.getByName(multicastInterface);
        MulticastSocket s = new MulticastSocket(port);

        s.joinGroup(new InetSocketAddress(mcastaddr, port), netIf);
        byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length, group);
        s.send(hi);
    }
    public void startThread() {
        (new Thread(this)).start();
    }
}