package service;

import java.util.Map;

public class Services {
    private Map<String, Service> services;
    private int frequency = 120;

    public Services(Map<String, Service> services) {
        this.services = services;
    }

    public void addService(String id, Service service) {
        services.put(id, service);
    }

    public Service getService(String id) {
        return services.get(id);
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
