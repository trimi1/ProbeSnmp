package thread;

import be.helmo.reseau.analyzer.Analyzer;
import service.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.*;

public class UnicastReceiver implements Runnable {
    private ServerSocket serverSocket;
    private int unicastPort;
    private UnicastSender unicastSender;

    public UnicastReceiver(int unicastPort, Services services) {
        this.unicastPort = unicastPort;
        this.unicastSender = new UnicastSender();
        startThread();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(unicastPort);
             Socket clientSocket = serverSocket.accept();
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("**UnicastReceiver : MESSAGE RECEIVED: \n" + inputLine);
                Analyzer analyzer = Analyzer.getInstance();
                String regexMessage = analyzer.giveMessageConstruction(inputLine);
                Pattern pattern = Pattern.compile(regexMessage);
                Matcher matcher = pattern.matcher(inputLine);
                if (pattern.equals(Pattern.compile(Analyzer.SETUP_REGEX))) {
                    addServicesAndFrequency(inputLine);
                } else if (inputLine.matches(Analyzer.STATUSOF_REGEX)) {
                    statusSender(inputLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //received SETUP : Stocker frequence et les AURLs dans Services
    private void addServicesAndFrequency(String message) {
        //foreach Services services.addService(aurl, service);
        // pour tout les aurl qui message.matches(Analyzer.AUGMENTED_URL_REGEX);, créer un service et l'ajouter à la liste des services
        //REGLER LES SOUCIS DE REGEX POUR QUE CA MATCH + du coup les matcher.group("truc")
//        Matcher matcherId = Pattern.compile(Analyzer.SETUP_REGEX).matcher(message);
//        String frequency = matcherId.group("frequency");
//        System.out.println(frequency);
//        String augmentedUrls = matcherId.group("augmentedUrls");
//        System.out.println(augmentedUrls);


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