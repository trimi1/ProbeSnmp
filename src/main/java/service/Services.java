package service;

import java.util.HashMap;
import java.util.Map;

public class Services {
    private static Map<String, Service> services = new HashMap<>();
    private static int frequency = 120;

    public static synchronized void addService(String id, Service service) {
        services.put(id, service);
    }

    public static Service getService(String id) {
        return services.get(id);
    }

    public static int getFrequency() {
        return frequency;
    }

    public static synchronized void setFrequency(int frequency) {Services.frequency = frequency;}
    public static boolean containsService(String id) {
        return services.containsKey(id);
    }
    public static synchronized Map<String, Service> getAllServices() {
        return new HashMap<>(services);
    }
}
