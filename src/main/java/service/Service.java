package service;

public class Service{
    String url;
    private int min;
    private int max;
    private ServiceStates state;

    public Service(String url, int min, int max, ServiceStates state) {
        this.url = url;
        this.min = min;
        this.max = max;
        this.state = state;
    }

    public String url() {
        return url;
    }

    public void setState(ServiceStates state) {
        this.state = state;
    }
}