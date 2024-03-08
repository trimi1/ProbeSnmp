package service;

public class Service{
    private String url;
    private String id;
    private int min;
    private int max;
    private ServiceStates state;

    public Service(String url, String id, int min, int max) {
        this.id = id;
        this.url = url;
        this.min = min;
        this.max = max;
        this.state = ServiceStates.UNKNOWN;
    }

    public String url() {
        return url;
    }

    public String id() {
        return id;
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