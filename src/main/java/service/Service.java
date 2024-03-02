package service;

public record Service(String host, int port, String securityName, String authPassphrase, String privPassphrase, String oid, int min, int max) {
}