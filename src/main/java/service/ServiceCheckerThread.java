package service;

import thread.MulticastSender;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServiceCheckerThread implements Runnable {
    private MulticastSender multicastSender;
    private ServiceManager serviceManager;

    public ServiceCheckerThread(MulticastSender multicastSender) {
        this.multicastSender = multicastSender;
        this.serviceManager = new ServiceManager();
        startThread();
    }

    @Override
    public void run() {
        AtomicBoolean changed = new AtomicBoolean(false);
        while (true) {
            try {
                System.out.println("Checking services...");
                Services.getAllServices().forEach((id, service) -> {
                    System.out.println("Checking id " + id);
                    System.out.println("Service url: " + service.url());
                    ServiceStates oldStatus = service.getState();
                    ServiceStates newStatus = serviceManager.checkStatusService(service);
                    if (!oldStatus.equals(newStatus)) {
                        changed.set(true);
                    }
                });
                if(changed.get()) {
                    try {
                        multicastSender.sendDataMessage();
                        System.out.println("**MESSAGE DATA HAS BEEN SENT");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    changed.set(false);
                }
                Thread.sleep(Services.getFrequency() * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}