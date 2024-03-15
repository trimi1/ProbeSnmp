package snmp.handler;

import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IAnalyzer;
import config.Configuration;
import security.AES;
import security.IAES;
import snmp.Service;
import snmp.ServiceStates;
import snmp.Services;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicastReceiver implements Runnable {
    private final ServerSocket socket;
    private final Configuration config;
    private final SecureRandom secureRandom = new SecureRandom();

    public UnicastReceiver(int unicastPort, Configuration config) throws IOException {
        this.socket = new ServerSocket(unicastPort);
        this.config = config;
        startThread();
    }

    public void run() {
        while (true) {
            try(Socket monitorDaemonSocket = socket.accept();
                InputStreamReader inputStreamReader = new InputStreamReader(monitorDaemonSocket.getInputStream());
                OutputStream outputStream = monitorDaemonSocket.getOutputStream()) {
                System.out.println("**UnicastReceiver - MESSAGE RECEIVED : ");
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStreamReader.read()) != -1) {
                    analyzeMessage(buffer, bytesRead, outputStream);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void analyzeMessage(byte[] buffer, int bytesRead, OutputStream outputStream) throws Exception {
        IAnalyzer analyzer = Analyzer.getInstance();
        String messageSecret = decryptMesssage(buffer);
        Map<String, String> groups = analyzer.getGroups(messageSecret);
        if (!groups.isEmpty()) {
            if (groups.get("Type").equals("SETUP")) {
                System.out.println("Setup message received");
                int frequency = Integer.parseInt(groups.get("Frequency"));
                Services.setFrequency(frequency);
                String aurlgroup = groups.get("Aurls");
                ajoutAurlService(aurlgroup);
                System.out.println("Service : " + Services.getService("snmp1").url());
                System.out.println("Service : " + Services.getService("snmp2").url());
            } else if (groups.get("Type").equals("STATUSOF")) {
                System.out.println("StatusOf message received");
                String id = groups.get("Id");
                sendStatusRespond(id, Services.getService(id).getState(), outputStream);
            }
        }
    }
    private void ajoutAurlService(String aurl) {
        Pattern pattern = Pattern.compile(Analyzer.AUGMENTED_URL_REGEX);
        Matcher matcher = pattern.matcher(aurl);
        while (matcher.find()) {
            String url = matcher.group("Url");
            String id = matcher.group("Id");
            int min = Integer.parseInt(matcher.group("Min"));
            int max = Integer.parseInt(matcher.group("Max"));
            System.out.println();
            if(!Services.containsService(id)) {
                Services.addService(id, new Service(url,id,min,max));
                System.out.print("**UnicastReceiver - SERVICE ADDED : " + "id : " + id + " url : " + url + " min : " + min + " max : " + max);
            }
        }
    }
    private void sendStatusRespond(String id,ServiceStates states, OutputStream outputStream) throws Exception {
        String message = Builder.buildStatus(id, states.toString());
        byte[] messageSecret = encryptMesssage(message);
        outputStream.write(messageSecret);
        outputStream.flush();
    }
    private byte[] encryptMesssage(String messageSecret) throws Exception {
        IAES aes = new AES();
        byte[] key = Base64.getDecoder().decode(config.aesKey());
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        byte[] associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
        return aes.encrypt(messageSecret, secretKey, associatedData);
    }
    private String decryptMesssage(byte[] messageSecret) throws Exception {
        IAES aes = new AES();
        byte[] key = Base64.getDecoder().decode(config.aesKey());
            key = Arrays.copyOf(key, 32);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        byte[] associatedData = "ProtocolVersion1".getBytes(StandardCharsets.UTF_8);
        return aes.decrypt(messageSecret, secretKey, associatedData);
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}
