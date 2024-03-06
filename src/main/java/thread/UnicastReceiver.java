package thread;

import be.helmo.reseau.analyzer.Analyzer;
import service.Services;

import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static be.helmo.reseau.analyzer.Analyzer.SETUP_REGEX;

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
            String message = new String(buffer);
            System.out.println(message);//faudra recuperer la frequence et les AURLs pour les stocker dans Services
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

/*   public void run() {
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
    public void responseStatusOf() {
        IBuilder builder = new Builder();
        String message = builder.buildStatusOf(1);
        byte[] bytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    //received SETUP : Stocker frequence et les AURLs dans Services
    private void addServicesAndFrequency(String message) {
        //foreach Services services.addService(aurl, service);
        // pour tout les aurl qui message.matches(Analyzer.AUGMENTED_URL_REGEX);, créer un service et l'ajouter à la liste des services
        Matcher matcher = Pattern.compile(Analyzer.AUGMENTED_URL_REGEX).matcher(message);
        if (matcher.find()) {
            String frequency = matcher.group("frequency");
            String augmentedUrls = matcher.group("augmentedUrls");

        }
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