package snmpversions;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPv2 implements ISNMP{
    public SNMPv2() {
    }

    private CommunityTarget createDefault(String targetAddress, String community) {
        Address address = GenericAddress.parse(targetAddress);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(SnmpConstants.version2c);
        target.setTimeout(3 * 1000L);
        target.setRetries(3);
        return target;
    }

    public String getValue(String host, int port, String communityName, String oid) {
        String targetAddress = String.format("udp:%s/%d", host, port);
        CommunityTarget target = createDefault(targetAddress, communityName);
        Snmp snmp = null;
        StringBuffer result = null;
        try {
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);

            PDU response = respEvent.getResponse();

            if (response == null)
                result = null;

            else {
                for (int i = 0; i < response.size(); ++i) {
                    VariableBinding vb = response.get(i);
                    result = new StringBuffer();
                    result.append(vb.getVariable().toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (Exception e) {
                }
            }
            return result == null ? null : result.toString();
        }
    }
}