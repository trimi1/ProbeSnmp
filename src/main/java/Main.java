import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.builder.Builder;

import be.helmo.reseau.interfaces.IAnalyzer;
import config.ConfigReader;
import config.Configuration;
import service.*;
import service.ServiceCheckerThread;
import snmpversions.SNMPv2;
import thread.MulticastSender;
import thread.UnicastReceiver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        Configuration config = ConfigReader.getConfig(args[0]);// Lecture du fichier de configuration

//        // PAS SUPPRIMER ! ON DEVRA UTILISER .getValue POUR AVOIR LA VALEUR (CPU ou mémoire)
//        SNMPv2 snmpv2test = new SNMPv2();
//        String valueSNMPv2 = snmpv2test.getValue("trkr.swilabus.com", 161, "1amMemb3r0fTe4mSWILA", "1.3.6.1.4.1.2021.11.11.0");
//        System.out.println("IIIIIIIIIICIIIIIIIIII v2 :" + valueSNMPv2 +"\n");
//
//        // PAS SUPPRIMER ! ON DEVRA UTILISER .getValue POUR AVOIR LA VALEUR (CPU ou mémoire)
//        SNMPv3 snmpv3test = new SNMPv3(true);
//        String valueSNMPv3 = snmpv3test.getValue("v3.swi.la", 6161, "superswila", "iLikeGodSWILA2024", "TeamG0D$wila", "1.3.6.1.4.1.2021.4.11.0");
//        System.out.println("IIIIIIIIIICIIIIIIIIII v3 :" + valueSNMPv3 +"\n");


//        ConfigReader configReader = new ConfigReader("..\\ProbeSnmp\\src\\main\\java\\ressources\\configProbes.json");
//        var MulticastSending = new MulticastSender(configReader.getMulticastAddress(), configReader.getMulticastPort(), configReader.getMulticastInterface(), configReader.getProtocol(), configReader.getUnicastPort());
//        var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
//        scheduledExecutor.scheduleAtFixedRate(MulticastSending, 0, 5, TimeUnit.SECONDS);
//        System.out.println("MulticastSender started");

        //Lancement du thread qui envoie uniquement PROBE
        var multicastSending = new MulticastSender(config.multicastAddress(), config.multicastDelay(), config.multicastPort(), config.multicastInterface(), config.protocol(), config.unicastPort());
        multicastSending.startThread();
        //1) SIMULATION DU M.D. qui envoie la config via "SETUP frequency 0*100(sp augmented_url)"
        // A chaque "PROBE <PROTOCLE> <PORT>" envoyer par la Probe (90s) -> config envoyé par M.D.
        // il faut un Thread (UnicastReceiver) qui receptionne la config envoyé par le M.D.
        Builder builder = new Builder();
        String monitorSimulatedSetup = builder.buildSetup(120,
                List.of("snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999",
                        "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999"));
        int frequency = 120;

        //2) réception de la config (frenquency et AURLs)
        // dans le Thread (UnicastReceiver) concerné : il faudra dans un foreach, pour chaque aurl, créer un service et l'ajouter à la liste des services
        //Services services = new Services(new HashMap<>());
        Services.setFrequency(frequency);
        Service serviceSNMPv3 = new Service("snmp1://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0", "1.3.6.1.4.1.2021.4.11.0",10000, 99999999);
        Service serviceSNMPv2 = new Service("snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0","1.3.6.1.4.1.2021.11.11.0",10,99999999);
        Services.addService("snmp1", serviceSNMPv3);
        Services.addService("snmp2", serviceSNMPv2);

        UnicastReceiver lecteurNotifMD = new UnicastReceiver(config.unicastPort());
        //3) PROBE supervise/interroge les services et en capture les états (elle garde en mémoire l'état du service.)
        //4) changement d'un ETAT ? PROBE notifie par multicast le M.D. des changements d'états (DATA <PROTOCOLE> <PORT>)
        //var serviceChecker = new ServiceCheckerThread(services, MulticastSending);
        //ServiceManager serviceManager = new ServiceManager();
        //ServiceStates testState = serviceManager.checkStatusService(service);
        //System.out.println("STATE : "+testState);

        //5) SIMULATION M.D. : M.D. qui reçois cette notification, se connecte à la sonde
        //-> M.D. récupère les différents états en utilisant "statusof = "STATUSOF" sp id crlf "
        var UnicastReceiver = new UnicastReceiver(config.unicastPort());

        //////////////////////////// TESTS ////////////////////////////
        //test 1 : réussi (correction des regex)
        String aurl = "snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999";
            //Y'a un probleme chiant avec l'aurl qui ne match pas avec le regex AUGMENTED_URL_REGEX, donc peut faudra revoir AUGMENTED_URL_REGEX
        if (aurl.matches(Analyzer.AUGMENTED_URL_REGEX)){
            System.out.println("AURL MATCH");
        } else {
            System.out.println("AURL NOT MATCH");
        }

        /*// test 2 : Reussi mais uniquement avec le regex URL_REGEX et pas AUGMENTED_URL_REGEX
        IAnalyzer analyzer = new Analyzer();
        //String regexMessageUrl = analyzer.giveMessageConstruction(aurl);
        String regexMessageUrl = Analyzer.URL_REGEX;
        Matcher matcher = Pattern.compile(regexMessageUrl).matcher(aurl);
        if (matcher.find()) {
            System.out.println("PROTOCOLE : "+matcher.group("protocol"));
        }*/
    }
}
