
import config.ConfigReader;
import config.Configuration;
import service.*;
import service.ServiceCheckerThread;

import thread.MulticastSender;
import thread.UnicastReceiver;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        Configuration config = ConfigReader.getConfig(args[0]);// Lecture du fichier de configuration

        /*SecretKey aesKey = generateAESKey();

        byte[] keyBytes = aesKey.getEncoded();
        System.out.println("AES Key (Base64 Encoded): " + java.util.Base64.getEncoder().encodeToString(keyBytes));
        int byteCount = keyBytes.length;

        System.out.println("Nombre de bytes: " + byteCount);*/

        var multicastSending = new MulticastSender(config.multicastAddress(), config.multicastDelay(), config.multicastPort(), config.multicastInterface(), config.protocol(), config.unicastPort());
        multicastSending.startThread();

        UnicastReceiver lecteurNotifMD = new UnicastReceiver(config.unicastPort(), config);

        ServiceCheckerThread serviceChecker = new ServiceCheckerThread(multicastSending);

    }
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        // Create a KeyGenerator instance for AES
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        // Initialize the KeyGenerator with a key size of 256 bits
        keyGenerator.init(256);

        // Generate the AES key
        SecretKey secretKey = keyGenerator.generateKey();

        return secretKey;
    }
}
