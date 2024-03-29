package snmp.handler;

import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IAnalyzer;
import config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.AES;
import security.IAES;
import snmp.service.Service;
import snmp.service.ServiceStates;
import snmp.service.Services;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicastHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnicastHandler.class);
    private final ServerSocket socket;
    private final Configuration config;

    public UnicastHandler(int unicastPort, Configuration config) throws IOException {
        this.socket = new ServerSocket(unicastPort);
        this.config = config;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try(Socket monitorDaemonSocket = socket.accept();
                InputStream inputStream = monitorDaemonSocket.getInputStream();
                OutputStream outputStream = monitorDaemonSocket.getOutputStream()) {
                processIncomingMessages(inputStream, outputStream);
            } catch (Exception e) {
                LOGGER.error("Error in UnicastHandler", e);
            }
        }
    }

    private void processIncomingMessages(InputStream inputStream, OutputStream outputStream) throws Exception {
        while (true) {
            byte[] messageBytes = readMessage(inputStream);
            if (messageBytes == null) {
                break;
            }
            analyzeMessage(messageBytes, outputStream);
        }
    }

    private byte[] readMessage(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead == -1) {
            return null;
        }
        byte[] messageBytes = new byte[bytesRead];
        System.arraycopy(buffer, 0, messageBytes, 0, bytesRead);
        return messageBytes;
    }

    private void analyzeMessage(byte[] buffer, OutputStream outputStream) throws Exception {
        IAnalyzer analyzer = Analyzer.getInstance();
        String decryptedMesssage = decryptMesssage(buffer);
        LOGGER.info("Decrypted message received: " + decryptedMesssage.replace("\r\n", ""));
        Map<String, String> groups = analyzer.getGroups(decryptedMesssage);
        if (!groups.isEmpty()) {
            processMessageGroups(groups, outputStream);
        }
    }

    private void processMessageGroups(Map<String, String> groups, OutputStream outputStream) throws Exception {
        if (groups.get("Type").equals("SETUP")) {
            int frequency = Integer.parseInt(groups.get("Frequency"));
            Services.setFrequency(frequency);
            String aurlgroup = groups.get("Aurls");
            addAurlService(aurlgroup);
        } else if (groups.get("Type").equals("STATUSOF")) {
            String id = groups.get("Id");
            synchronizedSendStatusRespond(id, Services.getService(id).getState(), outputStream);
        }
    }

    private void addAurlService(String aurl) {
        Matcher matcher = Pattern.compile(Analyzer.AUGMENTED_URL_REGEX).matcher(aurl);
        while (matcher.find()) {
            String id = matcher.group("Id");
            if(!Services.containsService(id)) {
                Services.addService(id, new Service(matcher.group("Url"), id, Integer.parseInt(matcher.group("Min")), Integer.parseInt(matcher.group("Max"))));
                LOGGER.info("Service added: id={}, url={}, min={}, max={}", id, matcher.group("Url"), matcher.group("Min"), matcher.group("Max"));
            }
        }
    }

    synchronized private void synchronizedSendStatusRespond(String id,ServiceStates states, OutputStream outputStream) throws Exception {
        String message = Builder.buildStatus(id, states.toString());
        LOGGER.info("Decrypted StatusRespond message to send: " + message);
        byte[] encryptedMessage = encryptMessage(message);
        LOGGER.info("Encrypted StatusRespond message to send: " + new String(encryptedMessage, StandardCharsets.UTF_8));
        outputStream.write(encryptedMessage);
        outputStream.flush();
    }

    private byte[] encryptMessage(String messageSecret) throws Exception {
        IAES aes = new AES();
        byte[] key = Base64.getDecoder().decode(config.aesKey());
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        byte[] associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
        return aes.encrypt(messageSecret, secretKey, associatedData);
    }
    private String decryptMesssage(byte[] messageSecret) throws Exception {
        IAES aes = new AES();
        byte[] key = Base64.getDecoder().decode(config.aesKey());
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        byte[] associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
        return aes.decrypt(messageSecret, secretKey, associatedData);
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}
