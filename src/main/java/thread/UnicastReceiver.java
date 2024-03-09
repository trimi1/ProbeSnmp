package thread;

import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IAnalyzer;
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

    public UnicastReceiver(int unicastPort) throws IOException {
        this.socket = new ServerSocket(unicastPort);
        startThread();
    }
    public void run() {
        while (true) {
            try(Socket monitorDaemonSocket = socket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(monitorDaemonSocket.getInputStream()));
                PrintWriter writerToMonitor = new PrintWriter(monitorDaemonSocket.getOutputStream(), true)) {
                System.out.println("Message recu");
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
        IAnalyzer analyzer = Analyzer.getInstance();
        var optionalRegex = analyzer.getRegex(message);
        if (!optionalRegex.isEmpty()) {
            var regex = optionalRegex.get();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(message);
            if (pattern.pattern().equals(Analyzer.SETUP_REGEX) && matcher.find()) {
                int frequency = Integer.parseInt(matcher.group("Frequency"));
                Services.setFrequency(frequency);
                String aurlgroup = matcher.group("Aurls");
                ajoutAurlService(aurlgroup);
            } else if (pattern.pattern().equals(Analyzer.STATUSOF_REGEX) && matcher.find()) {
                String id = matcher.group("Id");
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
            if(!Services.containsService(id)) {
                Services.addService(id, new Service(url,id,min,max));
            }
        }
    }
    private void sendStatusRespond(String id,ServiceStates states, PrintWriter writerToMonitor) {
        String message = Builder.buildStatus(id, states.toString());
        writerToMonitor.write(message);
        writerToMonitor.flush();
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}
