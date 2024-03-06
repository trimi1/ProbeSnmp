package service;

import snmpversions.SNMPv2;
import snmpversions.SNMPv3;
import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.builder.Builder;
import be.helmo.reseau.interfaces.IAnalyzer;
import be.helmo.reseau.interfaces.IBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceManager {
    //checker si on a affaire à un service snmpv2 ou v3 (checker pour le "#" dans url qui correspond à snmpv3)
    private boolean isVersionSnmpThree(Service service){
        return service.url().contains("#");
    }

    //obtenir la valeur du service (à l'aide d'oid -> type de donnée à récupérer)
    private String getValue(Service service){
        String value = null;
        if (isVersionSnmpThree(service)){
            SNMPv3 snmpv3 = new SNMPv3(true);
            //matcher avec Service.url() pour récupérer les valeurs :
//            IAnalyzer analyzer = new Analyzer();
//            String regexMessageUrl = analyzer.giveMessageConstruction(service.url());
//            Matcher matcher = Pattern.compile(regexMessageUrl).matcher(service.url());
//            String value = snmpv3.getValue(matcher.group("host"), Integer.parseInt(matcher.group("port")), matcher.group("username"), matcher.group("password"), matcher.group("auth"), matcher.group("path"));
            value = snmpv3.getValue("v3.swi.la", 6161, "superswila", "iLikeGodSWILA2024", "TeamG0D$wila", service.oid());
            // A REMPLACER PAR : value = snmpv3.getValue(matcher.group("host"), Integer.parseInt(matcher.group("port")), ... service.oid());
        } else {
            SNMPv2 snmpv2 = new SNMPv2();
            value = snmpv2.getValue("trkr.swilabus.com", 161, "1amMemb3r0fTe4mSWILA", service.oid());
            // A REMPLACER PAR : value = snmpv2.getValue(matcher.group("host"), Integer.parseInt(matcher.group("port")), ... service.oid());
        }
        return value;
    }

    //checker si on dépasse le seuil max ou min (pas tenir compte borne max si = 99999999)
    private boolean isValueOutOfThreshold(Service service, String value){
        if (value != null){
            if (service.max() != 99999999){
                if (Integer.parseInt(value) > service.max()){
                    return true;
                } else return Integer.parseInt(value) < service.min();
            } else {
                return Integer.parseInt(value) < service.min();
            }
        }
        return false;
    }

    //checker l'état du service (OK, ALARM, DOWN) et mettre à jour l'état du service
    public ServiceStates checkStatusService(Service service) {
        ServiceStates state;

        String value = getValue(service);

        // A AMELIORER/REFAIRE : DOWN pour indiquer que la connexion au service n’est pas possible (service inaccessible ou code d’erreur inattendu)
        if (value == null) {
            state = ServiceStates.DOWN;
        } else if (isValueOutOfThreshold(service, value)){
            state = ServiceStates.ALARM;
        } else {
            state = ServiceStates.OK;
        }

        service.setState(state);
        return state;
    }
}
