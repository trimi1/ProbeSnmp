package config;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigReader {
    public static Configuration getConfig(String path) throws IOException {
        byte[] jsonData = Files.readAllBytes(Paths.get(path));
        String content = new String(jsonData);
        JSONObject json = new JSONObject(content);
        return new Configuration(
                json.getString("protocol"),
                json.getString("multicastAddress"),
                json.getInt("multicastPort"),
                json.getString("multicastInterface"),
                json.getInt("multicastDelay"),
                json.getInt("unicastPort"),
                json.getString("aesKey")
        );
    }
}
