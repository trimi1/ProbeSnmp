package thread;

import be.helmo.reseau.builder.Builder;

import java.io.PrintWriter;
import java.net.*;
import java.util.List;

public class UnicastSender {
    public static void main(String[] args) {
        System.out.println("UnicastSender started...");
        try {
            String receiverHost = "localhost";
            int receiverPort = 60000;
            //test message
            String message = Builder.buildSetup(20,
                    List.of("snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999",
                            "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999"));
            System.out.println("Message to send: " + message);
            try (Socket socket = new Socket(receiverHost, receiverPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.write(message);
            }
            String message2 = Builder.buildStatusOf("snmp1");
            System.out.println("Message to send: " + message);
            try (Socket socket = new Socket(receiverHost, receiverPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.write(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}