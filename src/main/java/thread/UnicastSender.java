package thread;

import be.helmo.reseau.builder.Builder;
import security.AES;
import security.IAES;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
            byte[] messageSecret = encryptMesssage(message);
            String mm = new String(messageSecret, StandardCharsets.UTF_8);
            System.out.println("MessageSecret to send1: " + mm);
            try (Socket socket = new Socket(receiverHost, receiverPort);
                 OutputStream outputStream = socket.getOutputStream()){
                outputStream.write(messageSecret);
            }
            String message2 = Builder.buildStatusOf("snmp1");
            System.out.println("Message to send: " + message2);
            messageSecret = encryptMesssage(message);
            String mm2 = new String(messageSecret, StandardCharsets.UTF_8);
            System.out.println("MessageSecret to send2: " + mm2);
            try (Socket socket = new Socket(receiverHost, receiverPort);
                 OutputStream outputStream = socket.getOutputStream()){
                outputStream.write(messageSecret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static byte[] encryptMesssage(String messageSecret) throws Exception {
        IAES aes = new AES();
        byte[] key = Base64.getDecoder().decode("EPCtxxGqG1HUvD1LWaAbjLp+iU57QoKtcwn/yX8yM0g=");
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        byte[] associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
        return aes.encrypt(messageSecret, secretKey, associatedData);
    }
}