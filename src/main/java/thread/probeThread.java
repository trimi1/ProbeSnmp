package thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class probeThread implements Runnable{

    private Thread probeThread;

    public probeThread() {
        probeThread = new Thread(this);
        probeThread.start();
    }
    public void run() {
        try (MulticastSocket socket = new MulticastSocket()) {
            //try(BufferedReader reader = Files.newBufferedReader(Paths.get("src/ressouces/Config.ini"))){
            String MultiCastMessage = buildProbe(" 224.0.0.254", 60202);
            InetAddress group = InetAddress.getByName(" 224.0.0.254");
            byte[] message = MultiCastMessage.getBytes();
            DatagramPacket packet = new DatagramSocket(message, message.length,group,6022);

            socket.send(packet);
            socket.close();
            /*}catch(IOException e){
                e.printStackTrace();
            }*/

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void startThread() {
        (new Thread(this)).start();
    }
}

