package service;

import snmpversions.SNMPv2;
import snmpversions.SNMPv3;
import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.interfaces.IAnalyzer;

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
            value = getValueSNMP3(service.url());
        } else {
            SNMPv2 snmpv2 = new SNMPv2();
            value = getValueSNMP2(service.url());
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

    private String getValueSNMP2(String url){
        String host = null;
        String oid = null;
        int port = 0;
        String username = null;

        SNMPv2 snmpv2 = new SNMPv2();
        IAnalyzer analyzer = Analyzer.getInstance();
        String regexMessage = analyzer.giveMessageConstruction(url);
        Pattern pattern = Pattern.compile(regexMessage);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()){
            host = matcher.group("Host");
            oid = matcher.group("Oid");
            port = Integer.parseInt(matcher.group("Port"));
            username = matcher.group("Username");
        }
        return snmpv2.getValue(host, port, username, oid);
    }

    private String getValueSNMP3(String url){
        String host = null;
        String oid = null;
        int port = 0;
        String password = null;
        String username = null;
        String authentificateur = null;

        SNMPv3 snmpv3 = new SNMPv3(true);
        IAnalyzer analyzer = Analyzer.getInstance();
        String regexMessage = analyzer.giveMessageConstruction(url);
        Pattern pattern = Pattern.compile(regexMessage);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()){
            host = matcher.group("Host");
            oid = matcher.group("Oid");
            port = Integer.parseInt(matcher.group("Port"));
            password = matcher.group("Password");
            username = matcher.group("Username");
            authentificateur = matcher.group("Auth");
        }
        return snmpv3.getValue(host, port, username, authentificateur, password, oid);
    }
}
