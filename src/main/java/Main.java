
import config.ConfigReader;
import config.Configuration;
import snmp.handler.ServiceHandler;

import snmp.handler.MulticastHandler;
import snmp.handler.UnicastHandler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Configuration config = ConfigReader.getConfig(args[0]);

        var multicastSending = new MulticastHandler(config.multicastAddress(), config.multicastDelay(), config.multicastPort(), config.multicastInterface(), config.protocol(), config.unicastPort());
        multicastSending.startThread();

        var unicastHandler = new UnicastHandler(config.unicastPort(), config);
        unicastHandler.startThread();

        var serviceHandler = new ServiceHandler(multicastSending);
        serviceHandler.startThread();
    }
}
