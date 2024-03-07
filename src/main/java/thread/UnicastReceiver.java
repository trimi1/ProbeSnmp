package thread;

import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IAnalyzer;
import be.helmo.reseau.interfaces.IBuilder;
import service.Service;
import service.ServiceStates;
import service.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicastReceiver implements Runnable {
    private final ServerSocket socket;
    //private Services services = new Services(new HashMap<>());

    public UnicastReceiver(int unicastPort) throws IOException {
        this.socket = new ServerSocket(unicastPort);
        startThread();
    }
    public void run() {
        while (true) {
            try(Socket monitorDaemonSocket = socket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(monitorDaemonSocket.getInputStream()));
            PrintWriter writerToMonitor = new PrintWriter(monitorDaemonSocket.getOutputStream(), true)) {
                String received = reader.readLine();
                if(received != null){
                    analyzeMessage(received, writerToMonitor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void analyzeMessage(String message,PrintWriter writerToMonitor) {
        IAnalyzer analyzer = new Analyzer();
        String regexMessage = analyzer.giveMessageConstruction(message);
        Pattern pattern = Pattern.compile(regexMessage);
        Matcher matcher = pattern.matcher(message);
        if(pattern.equals(Pattern.compile(Analyzer.SETUP_REGEX))) {
            while(matcher.find()) {
                int frequency = Integer.parseInt(matcher.group("Frequency"));
                Services.setFrequency(frequency);//transformation methode static
                //services.setFrequency(frequency);
                String aurl = matcher.group("Aurls");
                ajoutAurlService(aurl);
            }
        }else if(pattern.equals(Pattern.compile(Analyzer.STATUSOF_REGEX))) {
            if(matcher.find()){
                String id = matcher.group("Id");
                sendStatusRespond(id, /*services.getService(id).getState()*/Services.getService(id).getState(),writerToMonitor);
            }
        }
    }
    private void ajoutAurlService(String aurl) {
        String id = null;
        String url = null;
        int min = 0;
        int max =0;
        Pattern pattern = Pattern.compile(Analyzer.AUGMENTED_URL_REGEX);
        Matcher matcher = pattern.matcher(aurl);
        while (matcher.find()) {
            url = matcher.group("url");
            id = matcher.group("id");
            min = Integer.parseInt(matcher.group("min"));
            max = Integer.parseInt(matcher.group("max"));
        }
        if(!/*services.containsService(id)*/Services.containsService(id)) {
            //services.addService(id, new Service(url,id,min,max));
            Services.addService(id, new Service(url,id,min,max));
        }
    }
    private void sendStatusRespond(String id,ServiceStates states, PrintWriter writerToMonitor) {
        IBuilder builder = new Builder();
        String message = builder.buildStatus(id, states.toString());
        writerToMonitor.write(message);
        writerToMonitor.flush();
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}