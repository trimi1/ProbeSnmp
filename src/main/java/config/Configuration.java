package config;

public record Configuration(String protocol, String multicastAddress, int multicastPort, String multicastInterface, int multicastDelay, int unicastPort, String aesKey) {
}
