
import config.ConfigReader;
import config.Configuration;
import service.*;
import service.ServiceCheckerThread;

import thread.MulticastSender;
import thread.UnicastReceiver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Configuration config = ConfigReader.getConfig(args[0]);// Lecture du fichier de configuration

        var multicastSending = new MulticastSender(config.multicastAddress(), config.multicastDelay(), config.multicastPort(), config.multicastInterface(), config.protocol(), config.unicastPort());
        multicastSending.startThread();

        UnicastReceiver lecteurNotifMD = new UnicastReceiver(config.unicastPort(), config);

        ServiceCheckerThread serviceChecker = new ServiceCheckerThread(multicastSending);

    }
}
