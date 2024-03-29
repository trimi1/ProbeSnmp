package snmp.handler;

import snmp.service.ServiceManager;
import snmp.service.ServiceStates;
import snmp.service.Services;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceHandler.class);
    private final MulticastHandler multicastHandler;
    private final ServiceManager serviceManager;

    public ServiceHandler(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.serviceManager = new ServiceManager();
    }

    @Override
    public void run() {
        AtomicBoolean changed = new AtomicBoolean(false);
        while (!Thread.interrupted()) {
            try {


                LOGGER.info("- - - - Checking services... - - - - -");
                Services.getAllServices().forEach((id, service) -> {
                    ServiceStates oldStatus = service.getState();
                    ServiceStates newStatus = serviceManager.checkStatusService(service);
                    if (!oldStatus.equals(newStatus)) {
                        changed.set(true);
                    }
                });
                if(changed.get()) {
                    try {
                        sendDataMessage();
                    } catch (IOException e) {
                        LOGGER.error("Error sending data message", e);
                    }
                    changed.set(false);
                }
                Thread.sleep(Services.getFrequency() * 1000L);
            } catch (InterruptedException e) {
                LOGGER.error("ServiceHandler interrupted", e);
            }
        }
    }

    synchronized public void sendDataMessage() throws IOException {
        multicastHandler.sendDataMessage();
    }

    public void startThread() {
        (new Thread(this)).start();
    }
}