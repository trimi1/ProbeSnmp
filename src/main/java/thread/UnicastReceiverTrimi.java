package thread;

import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.interfaces.IAnalyzer;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicastReceiverTrimi implements Runnable {
    private DatagramSocket socket;
    private int unicastPort;

    public UnicastReceiverTrimi(int unicastPort) {
        this.unicastPort = unicastPort;
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
                //statusof : demander l'état d'un service et envoyer via unicast
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
    }


    public void startThread() {
        (new Thread(this)).start();
    }
}