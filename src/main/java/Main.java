
import config.ConfigReader;
import config.Configuration;
import snmp.ServiceCheckerThread;

import snmp.handler.MulticastSender;
import snmp.handler.UnicastReceiver;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        Configuration config = ConfigReader.getConfig(args[0]);// Lecture du fichier de configuration

        /*SecretKey aesKey = generateAESKey();

        byte[] keyBytes = aesKey.getEncoded();
        System.out.println("AES Key (Base64 Encoded): " + java.util.Base64.getEncoder().encodeToString(keyBytes));
        int byteCount = keyBytes.length;

        System.out.println("Nombre de bytes: " + byteCount);*/
        /*String str = "SETUP 20 snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999 snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999";
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        System.out.println("taille : " +bytes.length);
        String message = new String(bytes, StandardCharsets.UTF_8);
        System.out.print("message : " +message);*/

        var multicastSending = new MulticastSender(config.multicastAddress(), config.multicastDelay(), config.multicastPort(), config.multicastInterface(), config.protocol(), config.unicastPort());
        multicastSending.startThread();

        UnicastReceiver lecteurNotifMD = new UnicastReceiver(config.unicastPort(), config);

        ServiceCheckerThread serviceChecker = new ServiceCheckerThread(multicastSending);

    }
    /*public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        // Create a KeyGenerator instance for AES
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        // Initialize the KeyGenerator with a key size of 256 bits
        keyGenerator.init(256);

        // Generate the AES key
        SecretKey secretKey = keyGenerator.generateKey();

        return secretKey;
    }*/
}
