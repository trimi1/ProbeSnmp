package snmp.service;

import snmpversions.SNMPv2;
import snmpversions.SNMPv3;
import be.helmo.reseau.analyzer.Analyzer;
import be.helmo.reseau.interfaces.IAnalyzer;

import java.util.Map;

public class ServiceManager {
    private IAnalyzer analyzer = Analyzer.getInstance();

    public ServiceStates checkStatusService(Service service) {
        String value = getValue(service);
        ServiceStates state;

        if (value == null) {
            state = ServiceStates.DOWN;
        } else if (service.max() != 99999999 && (Integer.parseInt(value) > service.max() || Integer.parseInt(value) < service.min())) {
            state = ServiceStates.ALARM;
        } else {
            state = ServiceStates.OK;
        }

        service.setState(state);
        return state;
    }

    private String getValue(Service service){
        Map<String, String> groups = analyzer.getGroups(service.url());
        String host = groups.get("Host");
        String oid = groups.get("Oid");
        int port = Integer.parseInt(groups.get("Port"));
        String username = groups.get("Username");

        if (service.url().contains("#")) {// SNMPv3
            String password = groups.get("Password");
            String authentificateur = groups.get("Auth");
            SNMPv3 snmpv3 = new SNMPv3(true);
            return snmpv3.getValue(host, port, username, authentificateur, password, oid);
        } else {// SNMPv2
            SNMPv2 snmpv2 = new SNMPv2();
            return snmpv2.getValue(host, port, username, oid);
        }
    }
}