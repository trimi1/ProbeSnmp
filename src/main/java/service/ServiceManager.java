package service;

import snmpversions.SNMPv2;
import snmpversions.SNMPv3;

public class ServiceManager {
    //checker si on a affaire à un service snmpv2 ou v3 (checker pour le "#" dasn url qui correspond à snmpv3)
    private boolean isVersionSnmpThree(Service service){
        return service.url().contains("#");
    }

    //checker si on dépasse le seuil max ou min
    /*
    if (value != null){
                if (Integer.parseInt(value) > service.max()){
                    return "ALARM";
                } else if (Integer.parseInt(value) < service.min()){
                    return "DOWN";
                } else {
                    return "OK";
                }
            }
     */
    private boolean checkThreshold(Service service, String value){
        if (value != null){
            if (Integer.parseInt(value) > service.max()){
                return true;
            } else if (Integer.parseInt(value) < service.min()){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public String checkStatusService(Service service) {
        if (isVersionSnmpThree(service)){
            SNMPv3 snmpv3 = new SNMPv3(true);
            String value = snmpv3.getValue(service.url().split(":")[1], service.url().split(":")[2].split("#")[0], service.url().split(":")[2].split("#")[1], service.securityName(), service.authPassphrase(), service.privPassphrase(), service.oid());
            if (checkThreshold(service, value)){
                return "ALARM";
            } else {
                return "OK";
            }
        } else {
            SNMPv2 snmpv2 = new SNMPv2();
            String value = snmpv2.getValue(service.url().split(":")[1], Integer.parseInt(service.url().split(":")[2].split("/")[0]), service.url().split(":")[1], service.id());
            if (checkThreshold(service, value)){
                return "ALARM";
            } else {
                return "OK";
            }
        }
    }
/*
        // PAS SUPPRIMER ! ON DEVRA UTILISER .getValue POUR AVOIR LA VALEUR (CPU ou mémoire)
        SNMPv2 snmpv2test = new SNMPv2();
        String valueSNMPv2 = snmpv2test.getValue("trkr.swilabus.com", 161, "1amMemb3r0fTe4mSWILA", "1.3.6.1.4.1.2021.11.11.0");
        System.out.println("IIIIIIIIIICIIIIIIIIII v2 :" + valueSNMPv2 +"\n");

        // PAS SUPPRIMER ! ON DEVRA UTILISER .getValue POUR AVOIR LA VALEUR (CPU ou mémoire)
        SNMPv3 snmpv3test = new SNMPv3(true);
        String valueSNMPv3 = snmpv3test.getValue("v3.swi.la", 6161, "superswila", "iLikeGodSWILA2024", "TeamG0D$wila", "1.3.6.1.4.1.2021.4.11.0");
        System.out.println("IIIIIIIIIICIIIIIIIIII v3 :" + valueSNMPv3 +"\n");
 */
}