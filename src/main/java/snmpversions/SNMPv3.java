package snmpversions;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.fluent.SnmpBuilder;
import org.snmp4j.fluent.TargetBuilder;
import org.snmp4j.log.ConsoleLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.log.LogLevel;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.List;


public class SNMPv3 implements ISNMP {
    public SNMPv3(boolean debug) {
        if (debug) {
            ConsoleLogFactory consoleLogFactory = new ConsoleLogFactory();
            consoleLogFactory.getRootLogger().setLogLevel(LogLevel.DEBUG);
            LogFactory.setLogFactory(consoleLogFactory);
        }
    }
    /**
     * Get the value of the OID from the SNMP agent
     * @param address the address of the SNMP agent
     * @param contextName the context name
     * @param securityName the security name
     * @param authPassphrase the authentication passphrase
     * @param privPassphrase the privacy passphrase
     * @param oid the OID
     * @return the value of the OID
     * @throws IOException
     */
    private String getOID(String address, String contextName, String securityName,
                          String authPassphrase, String privPassphrase, String oid) throws IOException {
        SnmpBuilder snmpBuilder = new SnmpBuilder();
        Snmp snmp = snmpBuilder.udp().securityProtocols(SecurityProtocols.SecurityProtocolSet.maxCompatibility).v3().usm().threads(2).build();
        snmp.listen();
        Address targetAddress = GenericAddress.parse(address);
        byte[] targetEngineID = snmp.discoverAuthoritativeEngineID(targetAddress, 1000);
        if (targetEngineID != null) {
            TargetBuilder<?> targetBuilder = snmpBuilder.target(targetAddress);
            Target<?> userTarget = targetBuilder
                    .user(securityName, targetEngineID)
                    .auth(TargetBuilder.AuthProtocol.sha1).authPassphrase(authPassphrase)
                    .priv(TargetBuilder.PrivProtocol.aes128).privPassphrase(privPassphrase)
                    .done()
                    .timeout(3 * 1000L).retries(1)
                    .build();

            PDU pdu = targetBuilder.pdu().type(PDU.GET).oids(oid).contextName(contextName).build();
            ResponseEvent responseEventSnmp = snmp.get(pdu, userTarget);
            if (responseEventSnmp.getResponse() != null) {
                StringBuilder result = new StringBuilder();
                List<VariableBinding> vbs = responseEventSnmp.getResponse().getAll();
                for (VariableBinding vb : vbs) {
                    result.append(vb.getVariable().toString());
                }
                snmp.close();
                return result.toString();
            } else {
                return null;
            }
        } else {
            System.err.println("Timeout on engine ID discovery for " + targetAddress + ", GET not sent.");
            snmp.close();
            return null;
        }

    }

    /**
     * Get the value of the OID from the SNMP agent -> for example :
     * @param host the host of the SNMP agent
     * @param port the port of the SNMP agent
     * @param securityName the security name
     * @param authPassphrase the authentication passphrase
     * @param privPassphrase the privacy passphrase
     * @param oid the OID
     * @return the value of the OID
     */
    public String getValue(String host, int port, String securityName, String authPassphrase, String privPassphrase, String oid) {
        String targetAddress = String.format("udp:%s/%d", host, port);
        try {
            return getOID(targetAddress, "", securityName, authPassphrase, privPassphrase, oid);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }
}