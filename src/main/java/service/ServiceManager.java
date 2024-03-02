public class ServiceManager {
    //checker si on a affaire à un service snmpv2 ou v3 (checker pour le "#" dasn url qui correspond à snmpv3)
    private boolean isVersionSnmpThree(Service service){
        return service.url().contains("#");
    }

    public String checkstatusService(Service service) {
        int min = service.min();
        int max = service.max();
        if (isVersionSnmpThree(service)){

        }
        return null;
    }

}