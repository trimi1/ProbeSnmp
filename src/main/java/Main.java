import be.helmo.reseau.builder.Builder;

import config.ConfigReader;
import config.Configuration;
import service.Services;
import thread.MulticastSender;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        // Lecture du fichier de configuration
        Configuration config = ConfigReader.getConfig(args[0]);

        var MulticastSending = new MulticastSender(config.multicastAddress(), config.multicastPort(), config.multicastInterface(), config.protocol(), config.unicastPort());
        var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(MulticastSending, 0, 5, TimeUnit.SECONDS);

        //1) SIMULATION DU M.D. qui envoie la config via "SETUP frequency 0*100(sp augmented_url)"
        // A chaque "PROBE <PROTOCLE> <PORT>" reçu par la Probe (90s) -> config envoyé par M.D.
        // Il faut un thread qui receptionne la config envoyé par le M.D.
        Builder builder = new Builder();
        String monitorSimulatedSetup = builder.buildSetup(120,
                List.of("snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999",
                        "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999"));
        int frequency = 120;

        //2) réception de la config (frenquency et AURLs)
        Services services = new Services(new HashMap<>());
        services.setFrequency(frequency);
        //Service service = new Service("v3.swi.la", 6161, "superswila", "iLikeGodSWILA2024", "TeamG0D$wila", "1.3.6.1.4.1.2021.4.11.0", 10000, 99999999);
        //"v3.swi.la", 6161, "superswila", "iLikeGodSWILA2024", "TeamG0D$wila"

        //3) PROBE supervise/interroge les services et en capture les états (elle garde en mémoire l'état du service.)

        //4) changement d'un ETAT ? PROBE notifie par multicast le M.D. des changements d'états (DATA <PROTOCOLE> <PORT>)

        //5) SIMULATION M.D. : M.D. qui reçois cette notification, se connecte à la sonde
        //-> M.D. récupère les différents états en utilisant "statusof = "STATUSOF" sp id crlf "

    }
}
