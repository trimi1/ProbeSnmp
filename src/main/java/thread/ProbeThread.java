package thread;

import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ProbeThread implements Runnable{

    private Thread probeThread;
    private int multicastPort;
    private String multicastAdress;
    public ProbeThread(String multicastAddress, int multicastPort) {
        this.multicastAdress = multicastAddress;
        this.multicastPort = multicastPort;
        probeThread = new Thread(this);
        probeThread.start();
    }
    public void run() {
        sendProbeMessage("SNMP", 60202);
        /*try (MulticastSocket socket = new MulticastSocket()) {
            Path configFilePath = Paths.get("C:\\Users\\2001j\\Reseaux\\projet\\ProbeSnmp\\src\\main\\java\\ressources\\configProbes.json");
            if (!Files.exists(configFilePath))
            {
                System.out.print("fichier introuvable");
                return;
            }
            String content = new String(Files.readAllBytes(configFilePath));
            JSONObject json = new JSONObject(content);


            String protocol = json.getString("protocol");
            String multicastAddress = json.getString("multicastAddress");
            int multicastPort = json.getInt("multicastPort");
            String multicastInterface = json.getString("multicastInterface");
            int multicastDelay = json.getInt("multicastDelay");
            int unicastPort = json.getInt("unicastPort");
            String aesKey = json.getString("aesKey");*/
            // join a Multicast group and send the group salutations
//            InetAddress group = InetAddress.getByName(multicastAddress);
//            Builder probeMessage = new Builder();
//            String MultiCastMessage = probeMessage.buildProbe("SNMP", 60202);

//            InetAddress mcastaddr = InetAddress.getByName(multicastAddress);

//            byte[] message = MultiCastMessage.getBytes();
//            DatagramPacket packet = new DatagramSocket(message, message.length, group, 6022);

//            socket.send(packet);
//            socket.close();
            /*}catch(IOException e){
                e.printStackTrace();
            }*/

        /*}catch(Exception e){
            e.printStackTrace();
        }*/
    }

    private void sendProbeMessage(String Protocol, int port){
        IBuilder builder = new Builder();
        String message = builder.buildProbe(Protocol, port);
        byte[] bytes = message.getBytes();
        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(multicastAdress);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, multicastPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataMessage(String Protocol, int port){
        IBuilder builder = new Builder();
        String message = builder.buildProbe(Protocol, port);
        byte[] bytes = message.getBytes();
        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(multicastAdress);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, multicastPort);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startThread() {
        (new Thread(this)).start();
    }
}