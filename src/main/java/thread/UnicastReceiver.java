package thread;

import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IAnalyzer;
import config.Configuration;
import security.AES;
import security.IAES;
import service.Service;
import service.ServiceStates;
import service.Services;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicastReceiver implements Runnable {
    private final ServerSocket socket;

    public UnicastReceiver(int unicastPort, Configuration config) throws IOException {
        this.socket = new ServerSocket(unicastPort);
        startThread();
    }
    public void run() {
        while (true) {
            try(Socket monitorDaemonSocket = socket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(monitorDaemonSocket.getInputStream()));
                PrintWriter writerToMonitor = new PrintWriter(monitorDaemonSocket.getOutputStream(), true)) {
                String received = reader.readLine();
                System.out.println("**UnicastReceiver - MESSAGE RECEIVED : " + received);
                if(received != null){
                    analyzeMessage(received, writerToMonitor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void analyzeMessage(String message,PrintWriter writerToMonitor) {
        IAnalyzer analyzer = Analyzer.getInstance();
        Map<String, String> groups = analyzer.getGroups(message);
        if (!groups.isEmpty()) {
            if (groups.get("Type").equals("SETUP")) {
                System.out.println("Frequence :" + groups.get("Frequency"));
                int frequency = Integer.parseInt(groups.get("Frequency"));
                Services.setFrequency(frequency);
                String aurlgroup = groups.get("Aurls");
                ajoutAurlService(aurlgroup);
            } else if (groups.get("Type").equals("STATUSOF")) {
                String id = groups.get("Id");
                sendStatusRespond(id, Services.getService(id).getState(), writerToMonitor);
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
    private void sendStatusRespond(String id,ServiceStates states, PrintWriter writerToMonitor) {
        String message = Builder.buildStatus(id, states.toString());
        /*IAES aes = new AES();
        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        aes.encrypt(message, config.getSecretKey());*/
        writerToMonitor.write(message);
        writerToMonitor.flush();
        System.out.print("**UnicastReceiver - MESSAGE STATUS SENT : " + message);
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}
