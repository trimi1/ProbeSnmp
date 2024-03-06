package thread;

import be.helmo.reseau.analyzer.Analyzer;
import service.Services;

import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicastReceiver implements Runnable {
    private DatagramSocket socket;
    private int unicastPort;
    private UnicastSender unicastSender;

    public UnicastReceiver(int unicastPort, Services services) {
        this.unicastPort = unicastPort;
        this.unicastSender = new UnicastSender();
        startThread();
    }

    @Override
    public void run() {
        try {
            int MAX_LEN = 1000;
            socket = new DatagramSocket(unicastPort);
            byte[] buffer = new byte[MAX_LEN];
            DatagramPacket packet = new DatagramPacket(buffer, MAX_LEN);
            socket.receive(packet);
            String message = new String(buffer).trim();
            System.out.println("**UnicastReceiver : MESSAGE RECEIVED: \n" + message);
            addServicesAndFrequency(message);
            if (message.matches(Analyzer.SETUP_REGEX)) {
                addServicesAndFrequency(message);
            } else if (message.matches(Analyzer.STATUSOF_REGEX)) {
                statusSender(message);
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    //received SETUP : Stocker frequence et les AURLs dans Services
    private void addServicesAndFrequency(String message) {
        //foreach Services services.addService(aurl, service);
        // pour tout les aurl qui message.matches(Analyzer.AUGMENTED_URL_REGEX);, créer un service et l'ajouter à la liste des services
        //REGLER LES SOUCIS DE REGEX POUR QUE CA MATCH + du coup les matcher.group("truc")
        Matcher matcherId = Pattern.compile(Analyzer.SETUP_REGEX).matcher(message);
        String frequency = matcherId.group("frequency");
        System.out.println(frequency);
        String augmentedUrls = matcherId.group("augmentedUrls");
        System.out.println(augmentedUrls);


//        Matcher matcherAugmentedUrl = Pattern.compile(Analyzer.AUGMENTED_URL_REGEX).matcher(message);
//        String frequency = matcherAugmentedUrl.group("frequency");
//        String augmentedUrls = matcherAugmentedUrl.group("augmentedUrls");
    }

    //received STATUSOF : Probe envoye STATUS via unicast après avoir reçu STATUSOF
    private void statusSender(String message) {
        Matcher matcherStatusOf = Pattern.compile(Analyzer.STATUSOF_REGEX).matcher(message);
        String id = matcherStatusOf.group("id");
        String state = matcherStatusOf.group("state");
        String status = "STATUS " + id + state;
        if (matcherStatusOf.find()) {
            id = matcherStatusOf.group("id");
            state = matcherStatusOf.group("state");
            status = "STATUS " + id + state;
        }
        unicastSender.sendUnicast(status, "60000", unicastPort);
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}