package thread;

import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IAnalyzer;
import be.helmo.reseau.interfaces.IBuilder;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static be.helmo.reseau.analyzer.Analyzer.SETUP_REGEX;

public class UnicastReceiver implements Runnable {

    private DatagramSocket socket;
    private List<String> augmentedUrl = new ArrayList<>();
    private int frequency;

    public UnicastReceiver(DatagramSocket socket) {
        this.socket = socket;
        startThread();
    }

    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            while (true) {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                IAnalyzer analyzer = new Analyzer();
                String regexMessage = analyzer.giveMessageConstruction(received);
                Pattern pattern = Pattern.compile(regexMessage);
                Matcher matcher = pattern.matcher(received);
                if(pattern.equals(Pattern.compile(Analyzer.STATUSOF_REGEX))) {
                    if (matcher.find()) {
                        frequency = Integer.parseInt(matcher.group(1));
                        while (matcher.find()) {
                            String urlsGroup = matcher.group(2);
                            List<String> augmentedUrl = Arrays.asList(urlsGroup.split(Analyzer.SP_REGEX));
                        }
                    }
                }else if(pattern.equals(Pattern.compile(Analyzer.STATUSOF_REGEX))) {
                    if (matcher.find()) {
                        int id = Integer.parseInt(matcher.group(1));

                    }

                }else if(pattern.equals(Pattern.compile(Analyzer.STATUSOF_REGEX))){

                }else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Fermez le socket ou effectuez d'autres opérations de nettoyage si nécessaire
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }

    }
    public void responseStatusOf(){
        IBuilder builder = new Builder();
        String message = builder.buildStatusOf(1);
        byte[] bytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}