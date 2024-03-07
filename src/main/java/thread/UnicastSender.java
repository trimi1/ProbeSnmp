package thread;

import be.helmo.reseau.builder.Builder;

import java.net.*;
import java.io.*;
import java.util.List;

public class UnicastSender {
//    public static void main(String[] args) {
//        try {
//            InetAddress receiverHost = InetAddress.getByName("localhost");
//            //int receiverPort = Integer.parseInt(args[1]);
//            int receiverPort = 60000;
//            //test message
//            IBuilder builder = new Builder();
//            String message = builder.buildSetup(120,
//                    List.of("snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999",
//                            "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999"));
//
//            DatagramSocket mySocket = new DatagramSocket();
//            byte[] buffer = message.getBytes();
//            DatagramPacket packet = new DatagramPacket (buffer, buffer.length, receiverHost, receiverPort); mySocket.send (packet);
//            mySocket.close();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//    }
    public static void main(String[] args) {
        try {
            String receiverHost = "localhost";
            int receiverPort = 60000;
            //test message
            Builder builder = new Builder();
            String message = builder.buildSetup(120,
                    List.of("snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999",
                            "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999"));

            try (Socket socket = new Socket(receiverHost, receiverPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUnicast(String message, String receiverHost, int receiverPort) {
        try {
            InetAddress receiver = InetAddress.getByName(receiverHost);
            DatagramSocket mySocket = new DatagramSocket();
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket (buffer, buffer.length, receiver, receiverPort);
            mySocket.send (packet);
            mySocket.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}