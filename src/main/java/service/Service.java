package service;

public class Service{
    String url;
    private String oid;
    private int min;
    private int max;
    private ServiceStates state;

    public Service(String url, String oid, int min, int max) {
        this.url = url;
        this.oid = oid;
        this.min = min;
        this.max = max;
        this.state = ServiceStates.UNKNOWN;
    }

    public String url() {
        return url;
    }

    public String oid() {
        return oid;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public ServiceStates getState() {
        return state;
    }

    public void setState(ServiceStates state) {
        this.state = state;
    }
}