import thread.probeThread;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        var MulticastSending = new probeThread();
        var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(MulticastSending, 0, 90000, TimeUnit.MILLISECONDS);
    }
}
