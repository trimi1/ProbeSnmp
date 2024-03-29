import be.helmo.reseau.builder.Builder;
import security.AES;
import security.IAES;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class UnicastSenderTest {
    private static final String RECEIVER_HOST = "localhost";
    private static final int RECEIVER_PORT = 60000;
    private static final String AES_KEY = "EPCtxxGqG1HUvD1LWaAbjLp+iU57QoKtcwn/yX8yM0g=";

    public static void main(String[] args) {
        try {
            String buildSetupMessage = Builder.buildSetup(20,
                    List.of("snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999",
                            "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999"));
            sendMessage(buildSetupMessage);

            String buildStatusOfMessage = Builder.buildStatusOf("snmp1");
            sendMessage(buildStatusOfMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(String message) throws Exception {
        byte[] messageSecret = encryptMesssage(message);
        try (Socket socket = new Socket(RECEIVER_HOST, RECEIVER_PORT);
             OutputStream outputStream = socket.getOutputStream()){
            outputStream.write(messageSecret);
        }
    }

    private static byte[] encryptMesssage(String messageSecret) throws Exception {
        IAES aes = new AES();
        byte[] key = Base64.getDecoder().decode(AES_KEY);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        byte[] associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
        return aes.encrypt(messageSecret, secretKey, associatedData);
    }
}