package thread;

import java.net.*;

public class UnicastReceiverTrimi implements Runnable {

    public UnicastReceiverTrimi() {
        startThread();
    }

    @Override
    public void run() {
        try {
            int MAX_LEN = 1000;
            int localPortNum = 60000;
            DatagramSocket mySocket = new DatagramSocket(localPortNum);
            byte[] buffer = new byte[MAX_LEN];
            DatagramPacket packet = new DatagramPacket(buffer, MAX_LEN);
            mySocket.receive(packet);
            String message = new String(buffer);
            System.out.println(message);
            mySocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}