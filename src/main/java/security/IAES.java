package security;

import javax.crypto.SecretKey;

public interface IAES {
    public byte[] encrypt(String plaintext, SecretKey secretKey, byte[] associatedData) throws Exception;

    public String decrypt(byte[] cipherMessage, SecretKey secretKey, byte[] associatedData) throws Exception;
}
