import snmpversions.*;
import thread.ProbeThread;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        String smnp1 = "snmp1!snmp://superswila:TeamG0D$wila#iLikeGodSWILA2024@v3.swi.la:6161/1.3.6.1.4.1.2021.4.11.0!10000!99999999";
        String smnp2 = "snmp2!snmp://1amMemb3r0fTe4mSWILA@trkr.swilabus.com:161/1.3.6.1.4.1.2021.11.11.0!10!99999999";

        SNMPv2 snmpv2test = new SNMPv2();
        String valueSNMPv2 = snmpv2test.getValue("trkr.swilabus.com", 161, "1amMemb3r0fTe4mSWILA", "1.3.6.1.4.1.2021.11.11.0");
        System.out.println("IIIIIIIIIICIIIIIIIIII v2 :" + valueSNMPv2 +"\n");

        SNMPv3 snmpv3test = new SNMPv3(true);
        String valueSNMPv3 = snmpv3test.getValue("v3.swi.la", 6161, "superswila", "iLikeGodSWILA2024", "TeamG0D$wila", "1.3.6.1.4.1.2021.4.11.0");
        System.out.println("IIIIIIIIIICIIIIIIIIII v3 :" + valueSNMPv3 +"\n");

        ConfigReader configReader = new ConfigReader("..\\ProbeSnmp\\src\\main\\java\\ressources\\configProbes.json");
        var MulticastSending = new ProbeThread(configReader.getMulticastAddress(), configReader.getMulticastPort(), configReader.getMulticastInterface());
        var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(MulticastSending, 0, 5, TimeUnit.SECONDS);

//        String content = new String(Files.readAllBytes(Paths.get("configProbes.json")));
//        JSONObject json = new JSONObject(content);
//        int multicastDelay = json.getInt("multicastDelay");
    }
}
