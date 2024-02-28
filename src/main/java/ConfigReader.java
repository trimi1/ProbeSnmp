import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigReader {
    private String protocol;
    private String multicastAddress;
    private int multicastPort;
    private String multicastInterface;
    private int multicastDelay;
    private int unicastPort ;
    private String aesKey;
    public ConfigReader(String file) throws IOException {
        Path configFilePath = Paths.get(file);
        if (!Files.exists(configFilePath))
        {
            throw new FileNotFoundException("Le fichier de configuration n'existe pas: " + file);
        }
        String content = new String(Files.readAllBytes(configFilePath));
        JSONObject json = new JSONObject(content);
        this.protocol = json.getString("protocol");
        this.multicastAddress = json.getString("multicastAddress");
        this.multicastPort = json.getInt("multicastPort");
        this.multicastInterface = json.getString("multicastInterface");
        this.multicastDelay = json.getInt("multicastDelay");
        this.unicastPort = json.getInt("unicastPort");
        this.aesKey = json.getString("aesKey");
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public String getMulticastInterface() {
        return multicastInterface;
    }

    public int getMulticastDelay() {
        return multicastDelay;
    }

    public int getUnicastPort() {
        return unicastPort;
    }

    public String getAesKey() {
        return aesKey;
    }
}
