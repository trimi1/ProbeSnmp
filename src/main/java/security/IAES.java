package security;

import javax.crypto.SecretKey;

public interface IAES {
    byte[] encrypt(String plaintext, SecretKey secretKey, byte[] associatedData) throws Exception;

    String decrypt(byte[] cipherMessage, SecretKey secretKey, byte[] associatedData) throws Exception;
}
