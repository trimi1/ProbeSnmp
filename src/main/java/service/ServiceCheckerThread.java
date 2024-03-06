package service;

import service.ServiceManager;
import service.ServiceStates;
import service.Services;
import thread.MulticastSender;

import java.io.IOException;

public class ServiceCheckerThread implements Runnable {
    private Services services;
    private MulticastSender multicastSender;
    //private Thread thread;
    private ServiceManager serviceManager;

    public ServiceCheckerThread(Services services, MulticastSender multicastSender) {
        this.services = services;
        this.multicastSender = multicastSender;
        this.serviceManager = new ServiceManager();
        startThread();
    }

    @Override
    public void run() {
        while (true) {
            try {
                services.getServices().forEach((id, service) -> {
                    ServiceStates oldStatus = service.getState();
                    ServiceStates newStatus = serviceManager.checkStatusService(service);

                    if (!oldStatus.equals(newStatus)) {
                        try {
                            multicastSender.sendDataMessage();
                            System.out.println("**MESSAGE DATA HAS BEEN SENT");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Thread.sleep(services.getFrequency() * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}