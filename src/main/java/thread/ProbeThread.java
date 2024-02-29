package thread;

//personal imports
import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IBuilder;

//java imports
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.List;


public class ProbeThread implements Runnable{
    private Thread probeThread;
    private int multicastPort;
    private String multicastAdress;
    private String multicastInterface;
    private IBuilder builder;

    public ProbeThread(String multicastAddress, int multicastPort, String multicastInterface) {
        this.multicastAdress = multicastAddress;
        this.multicastPort = multicastPort;
        this.multicastInterface = multicastInterface;
        this.builder = new Builder();
        this.probeThread = new Thread(this);
        this.probeThread.start();
    }

    public void run() {
        sendProbeMessage("SNMP", multicastPort);
    }

    private void multicastSender(String message) {
        byte[] bytes = message.getBytes();
        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(multicastAdress);
            NetworkInterface networkInterface = NetworkInterface.getByName(multicastInterface);
            socket.setNetworkInterface(networkInterface);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, multicastPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendProbeMessage(String protocol, int port){
        String message = builder.buildProbe(protocol, port);
        multicastSender(message);
    }

    public void sendDataMessage(String protocol, int port){
        String message = builder.buildData(protocol, port);
        multicastSender(message);
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}