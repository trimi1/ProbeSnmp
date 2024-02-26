import thread.probeThread;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) throws IOException {
        var MulticastSending = new probeThread();
        var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

        String content = new String(Files.readAllBytes(Paths.get("config-probes.json")));
        JSONObject json = new JSONObject(content);
        int multicastDelay = json.getInt("multicastDelay");
        scheduledExecutor.scheduleAtFixedRate(MulticastSending, 0, multicastDelay, TimeUnit.SECONDS);
    }
}
