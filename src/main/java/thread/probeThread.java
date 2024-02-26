package thread;

import be.helmo.reseau.builder.Builder;
import org.json.JSONObject;
import org.snmp4j.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.nio.file.Paths;


public class probeThread implements Runnable{

    private Thread probeThread;

    public probeThread() {
        probeThread = new Thread(this);
        probeThread.start();
    }
    public void run() {
        try (MulticastSocket socket = new MulticastSocket()) {
            String content = new String(Files.readAllBytes(Paths.get("config-probes.json")));
            JSONObject json = new JSONObject(content);

            String protocol = json.getString("protocol");
            String multicastAddress = json.getString("multicastAddress");
            int multicastPort = json.getInt("multicastPort");
            String multicastInterface = json.getString("multicastInterface");
            int multicastDelay = json.getInt("multicastDelay");
            int unicastPort = json.getInt("unicastPort");
            String aesKey = json.getString("aesKey");

            // join a Multicast group and send the group salutations
            InetAddress group = InetAddress.getByName(multicastAddress);
            Builder probeMessage = new Builder();
            String MultiCastMessage = probeMessage.buildProbe("SNMP", 60202);

            InetAddress mcastaddr = InetAddress.getByName(multicastAddress);

//            byte[] message = MultiCastMessage.getBytes();
//            DatagramPacket packet = new DatagramSocket(message, message.length, group, 6022);
//
//            socket.send(packet);
//            socket.close();
//            /*}catch(IOException e){
//                e.printStackTrace();
//            }*/

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void startThread() {
        (new Thread(this)).start();
    }
}